package com.phimhay.juanng.modules.catalog.controller;

import com.phimhay.juanng.common.response.ApiResponse;
import com.phimhay.juanng.modules.catalog.dto.CrawlSelectedRequest;
import com.phimhay.juanng.modules.catalog.dto.ExternalMovieListResponse;
import com.phimhay.juanng.modules.catalog.entity.SyncSource;
import com.phimhay.juanng.modules.catalog.service.MovieCrawlerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/catalog/sync")
@RequiredArgsConstructor
public class MovieCrawlerController {

    private final MovieCrawlerService movieCrawlerService;

    // --- CRUD Nguồn Phim (SyncSource) ---

    @GetMapping("/sources")
    public ApiResponse<List<SyncSource>> getAllSources() {
        List<SyncSource> sources = movieCrawlerService.getAllSources();
        return ApiResponse.<List<SyncSource>>builder()
                .code(1000)
                .message("Lấy danh sách nguồn phim thành công!")
                .result(sources)
                .build();
    }

    @GetMapping("/sources/{id}")
    public ApiResponse<SyncSource> getSourceById(@PathVariable Long id) {
        SyncSource source = movieCrawlerService.getSourceById(id);
        return ApiResponse.<SyncSource>builder()
                .code(1000)
                .message("Lấy thông tin nguồn phim thành công!")
                .result(source)
                .build();
    }

    @PostMapping("/sources")
    public ApiResponse<SyncSource> createSource(@RequestBody @Valid SyncSource source) {
        SyncSource created = movieCrawlerService.createSource(source);
        return ApiResponse.<SyncSource>builder()
                .code(1000)
                .message("Thêm mới nguồn phim thành công!")
                .result(created)
                .build();
    }

    @PutMapping("/sources/{id}")
    public ApiResponse<SyncSource> updateSource(@PathVariable Long id, @RequestBody @Valid SyncSource sourceDetails) {
        SyncSource updated = movieCrawlerService.updateSource(id, sourceDetails);
        return ApiResponse.<SyncSource>builder()
                .code(1000)
                .message("Cập nhật nguồn phim thành công!")
                .result(updated)
                .build();
    }

    @DeleteMapping("/sources/{id}")
    public ApiResponse<Void> deleteSource(@PathVariable Long id) {
        movieCrawlerService.deleteSource(id);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Xóa nguồn phim thành công!")
                .build();
    }

    // --- Nghiệp vụ Crawl & Đồng bộ ---

    @GetMapping("/sources/{id}/fetch-list")
    public ApiResponse<ExternalMovieListResponse> fetchMovieList(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page) {
        ExternalMovieListResponse listResponse = movieCrawlerService.fetchMovieList(id, page);
        return ApiResponse.<ExternalMovieListResponse>builder()
                .code(1000)
                .message("Tải danh sách phim xem trước thành công!")
                .result(listResponse)
                .build();
    }

    @PostMapping("/sources/{id}/crawl-selected")
    public ApiResponse<List<String>> syncSelectedMovies(
            @PathVariable Long id,
            @RequestBody @Valid CrawlSelectedRequest request) {
        List<String> successSlugs = movieCrawlerService.syncSelectedMovies(id, request.getSlugs());
        return ApiResponse.<List<String>>builder()
                .code(1000)
                .message("Hoàn thành đồng bộ danh sách phim được chọn!")
                .result(successSlugs)
                .build();
    }

    @PostMapping("/sources/{id}/crawl-page")
    public ApiResponse<List<String>> crawlPage(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page) {
        List<String> successSlugs = movieCrawlerService.crawlPage(id, page);
        return ApiResponse.<List<String>>builder()
                .code(1000)
                .message("Hoàn thành đồng bộ toàn bộ phim trang " + page + "!")
                .result(successSlugs)
                .build();
    }
}
