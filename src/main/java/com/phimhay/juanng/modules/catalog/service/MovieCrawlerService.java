package com.phimhay.juanng.modules.catalog.service;

import com.phimhay.juanng.common.exception.AppException;
import com.phimhay.juanng.common.exception.ResultCode;
import com.phimhay.juanng.modules.catalog.dto.ExternalMovieListResponse;
import com.phimhay.juanng.modules.catalog.dto.ExternalMovieResponse;
import com.phimhay.juanng.modules.catalog.entity.SyncSource;
import com.phimhay.juanng.modules.catalog.repository.SyncSourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieCrawlerService {

    private final SyncSourceRepository syncSourceRepository;
    private final MovieSyncService movieSyncService;
    private final RestTemplate restTemplate;

    // --- CRUD Nguồn Phim (SyncSource) ---

    public List<SyncSource> getAllSources() {
        return syncSourceRepository.findAll();
    }

    public SyncSource getSourceById(Long id) {
        return syncSourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cấu hình nguồn phim với ID: " + id));
    }

    public SyncSource createSource(SyncSource source) {
        if (source.getName() == null || source.getListUrlPattern() == null || source.getDetailUrlBase() == null) {
            throw new AppException(ResultCode.INVALID_INPUT);
        }
        return syncSourceRepository.save(source);
    }

    public SyncSource updateSource(Long id, SyncSource sourceDetails) {
        SyncSource source = getSourceById(id);
        source.setName(sourceDetails.getName());
        source.setListUrlPattern(sourceDetails.getListUrlPattern());
        source.setDetailUrlBase(sourceDetails.getDetailUrlBase());
        source.setActive(sourceDetails.isActive());
        return syncSourceRepository.save(source);
    }

    public void deleteSource(Long id) {
        SyncSource source = getSourceById(id);
        syncSourceRepository.delete(source);
    }

    // --- Nghiệp vụ Crawl & Đồng bộ ---

    /**
     * Lấy danh sách phim từ nguồn để hiển thị chọn lọc (không lưu DB)
     */
    public ExternalMovieListResponse fetchMovieList(Long sourceId, int page) {
        SyncSource source = getSourceById(sourceId);
        if (!source.isActive()) {
            throw new IllegalArgumentException("Nguồn phim '" + source.getName() + "' hiện đang tạm khóa.");
        }

        String listUrl = source.getListUrlPattern().replace("{page}", String.valueOf(page));
        log.info("Gọi lấy danh sách phim từ nguồn: {} (URL: {})", source.getName(), listUrl);

        try {
            ExternalMovieListResponse response = restTemplate.getForObject(listUrl, ExternalMovieListResponse.class);
            if (response == null || !response.isStatus()) {
                throw new RuntimeException("Phản hồi từ API nguồn không hợp lệ hoặc lỗi.");
            }
            return response;
        } catch (Exception e) {
            log.error("Lỗi khi gọi API danh sách phim: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể tải danh sách phim từ API nguồn: " + e.getMessage());
        }
    }

    /**
     * Đồng bộ danh sách các phim đã chọn dựa trên slugs
     */
    public List<String> syncSelectedMovies(Long sourceId, List<String> slugs) {
        SyncSource source = getSourceById(sourceId);
        if (!source.isActive()) {
            throw new IllegalArgumentException("Nguồn phim '" + source.getName() + "' hiện đang tạm khóa.");
        }

        log.info("Bắt đầu đồng bộ danh sách {} phim được chọn từ nguồn: {}", slugs.size(), source.getName());
        List<String> successSlugs = new ArrayList<>();

        for (String slug : slugs) {
            if (slug == null || slug.trim().isEmpty()) continue;
            String detailUrl = source.getDetailUrlBase() + slug;
            log.info("Tải chi tiết phim từ URL: {}", detailUrl);

            try {
                ExternalMovieResponse response = restTemplate.getForObject(detailUrl, ExternalMovieResponse.class);
                if (response != null && response.isStatus() && response.getMovie() != null) {
                    movieSyncService.syncMovie(response);
                    successSlugs.add(slug);
                    log.info("Đồng bộ thành công phim: {} (slug: {})", response.getMovie().getName(), slug);
                } else {
                    log.warn("Bỏ qua đồng bộ slug: {} vì phản hồi chi tiết trống hoặc thất bại.", slug);
                }
            } catch (Exception e) {
                log.error("Lỗi khi đồng bộ phim (slug: {}): {}", slug, e.getMessage(), e);
                // Bỏ qua lỗi để tiếp tục đồng bộ các phim khác trong hàng đợi
            }
        }

        source.setLastSyncedAt(LocalDateTime.now());
        syncSourceRepository.save(source);

        return successSlugs;
    }

    /**
     * Đồng bộ tự động toàn bộ phim của một trang từ nguồn
     */
    public List<String> crawlPage(Long sourceId, int page) {
        SyncSource source = getSourceById(sourceId);
        if (!source.isActive()) {
            throw new IllegalArgumentException("Nguồn phim '" + source.getName() + "' hiện đang tạm khóa.");
        }

        log.info("Bắt đầu crawl và đồng bộ toàn bộ phim trang {} từ nguồn: {}", page, source.getName());

        // 1. Lấy danh sách phim của trang
        ExternalMovieListResponse listResponse = fetchMovieList(sourceId, page);
        if (listResponse.getItems() == null || listResponse.getItems().isEmpty()) {
            log.warn("Không tìm thấy bộ phim nào trên trang {}", page);
            return new ArrayList<>();
        }

        List<String> slugs = listResponse.getItems().stream()
                .map(ExternalMovieListResponse.MovieListItemDto::getSlug)
                .toList();

        // 2. Đồng bộ các phim lấy được
        List<String> successSlugs = syncSelectedMovies(sourceId, slugs);

        // 3. Cập nhật lịch sử trang vừa đồng bộ
        source.setLastSyncedPage(page);
        source.setLastSyncedAt(LocalDateTime.now());
        syncSourceRepository.save(source);

        return successSlugs;
    }
}
