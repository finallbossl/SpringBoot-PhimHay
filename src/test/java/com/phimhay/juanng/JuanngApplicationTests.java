package com.phimhay.juanng;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phimhay.juanng.modules.catalog.dto.ExternalMovieListResponse;
import com.phimhay.juanng.modules.catalog.dto.ExternalMovieResponse;
import com.phimhay.juanng.modules.catalog.entity.Movie;
import com.phimhay.juanng.modules.catalog.entity.SyncSource;
import com.phimhay.juanng.modules.catalog.repository.*;
import com.phimhay.juanng.modules.catalog.service.MovieCrawlerService;
import com.phimhay.juanng.modules.catalog.service.MovieSyncService;
import com.phimhay.juanng.modules.streaming.repository.EpisodeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JuanngApplicationTests {

	@Autowired
	private MovieSyncService movieSyncService;
	@Autowired
	private MovieCrawlerService movieCrawlerService;
	@Autowired
	private MovieRepository movieRepository;
	@Autowired
	private EpisodeRepository episodeRepository;
	@Autowired
	private ActorRepository actorRepository;
	@Autowired
	private DirectorRepository directorRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private CountryRepository countryRepository;
	@Autowired
	private SyncSourceRepository syncSourceRepository;
	@Autowired
	private RestTemplate originalRestTemplate;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	void contextLoads() {
	}

	@Test
	void testMovieSync() throws Exception {
		try (InputStream is = getClass().getResourceAsStream("/test_payload.json")) {
			assertNotNull(is, "Không tìm thấy file test_payload.json trong classpath");
			ExternalMovieResponse response = objectMapper.readValue(is, ExternalMovieResponse.class);

			// Thực hiện đồng bộ lần 1
			Movie movie = movieSyncService.syncMovie(response);
			assertNotNull(movie);
			assertNotNull(movie.getId());
			assertEquals("Những Cô Gái Dễ Thương", movie.getName());
			assertEquals("nhung-co-gai-de-thuong", movie.getSlug());
			assertEquals(48068L, movie.getExternalId());
			assertEquals("1180702", movie.getTmdbId());
			assertEquals(5.4, movie.getTmdbVoteAverage());
			assertEquals(75, movie.getTmdbVoteCount());
			assertEquals("tt29288566", movie.getImdbId());

			// Xác nhận thể loại
			assertFalse(movie.getCategories().isEmpty(), "Categories không được trống");
			assertTrue(movie.getCategories().stream().anyMatch(c -> c.getSlug().equals("hanh-dong")));

			// Xác nhận diễn viên và đạo diễn
			assertFalse(movie.getActors().isEmpty(), "Actors không được trống");
			assertTrue(movie.getActors().stream().anyMatch(a -> a.getSlug().equals("alice-taglioni")));
			assertFalse(movie.getDirectors().isEmpty(), "Directors không được trống");
			assertTrue(movie.getDirectors().stream().anyMatch(d -> d.getSlug().equals("noemie-saglio")));

			// Xác nhận tập phim
			var episodeOpt = episodeRepository.findByMovieIdAndServerNameAndSlug(movie.getId(), "Vietsub #1", "full");
			assertTrue(episodeOpt.isPresent(), "Không tìm thấy tập phim đã lưu");
			var episode = episodeOpt.get();
			assertEquals("nice-girls-full", episode.getFilename());
			assertEquals("https://embed.phim.com/nice-girls", episode.getLinkEmbed());
			assertEquals("https://m3u8.phim.com/nice-girls/index.m3u8", episode.getLinkM3u8());

			// Thực hiện đồng bộ lần 2 để kiểm tra ghi đè (Option 1)
			Movie movie2 = movieSyncService.syncMovie(response);
			assertEquals(movie.getId(), movie2.getId(), "Đồng bộ lần 2 phải ghi đè lên cùng 1 bộ phim cũ");
		}
	}

	@Test
	void testSyncSourceCRUD() {
		// Create
		SyncSource source = SyncSource.builder()
				.name("test_crud_source")
				.listUrlPattern("http://example.com/list?page={page}")
				.detailUrlBase("http://example.com/detail/")
				.isActive(true)
				.build();
		SyncSource saved = syncSourceRepository.save(source);
		assertNotNull(saved.getId());
		assertEquals("test_crud_source", saved.getName());

		// Read
		SyncSource retrieved = syncSourceRepository.findById(saved.getId()).orElse(null);
		assertNotNull(retrieved);
		assertEquals("http://example.com/detail/", retrieved.getDetailUrlBase());

		// Update
		retrieved.setName("test_crud_source_updated");
		SyncSource updated = syncSourceRepository.save(retrieved);
		assertEquals("test_crud_source_updated", updated.getName());

		// Delete
		syncSourceRepository.delete(updated);
		assertFalse(syncSourceRepository.findById(saved.getId()).isPresent());
	}

	@Test
	void testMovieCrawlerAndFetch() throws Exception {
		// 1. Tạo nguồn phim thử nghiệm
		SyncSource source = SyncSource.builder()
				.name("vsmov_test_crawler")
				.listUrlPattern("https://vsmov_mock.com/api/list?page={page}")
				.detailUrlBase("https://vsmov_mock.com/api/detail/")
				.isActive(true)
				.build();
		source = syncSourceRepository.save(source);

		// 2. Thiết lập RestTemplate giả lập (Mock)
		RestTemplate mockRestTemplate = Mockito.mock(RestTemplate.class);
		ReflectionTestUtils.setField(movieCrawlerService, "restTemplate", mockRestTemplate);

		try (InputStream listIs = getClass().getResourceAsStream("/test_list_payload.json");
			 InputStream detailIs = getClass().getResourceAsStream("/test_payload.json")) {

			assertNotNull(listIs);
			assertNotNull(detailIs);

			ExternalMovieListResponse mockListResponse = objectMapper.readValue(listIs, ExternalMovieListResponse.class);
			ExternalMovieResponse mockDetailResponse = objectMapper.readValue(detailIs, ExternalMovieResponse.class);

			// Mock các cuộc gọi REST
			Mockito.when(mockRestTemplate.getForObject(
					"https://vsmov_mock.com/api/list?page=1",
					ExternalMovieListResponse.class)
			).thenReturn(mockListResponse);

			Mockito.when(mockRestTemplate.getForObject(
					"https://vsmov_mock.com/api/detail/nhung-co-gai-de-thuong",
					ExternalMovieResponse.class)
			).thenReturn(mockDetailResponse);

			// 3. Kiểm thử fetchMovieList (chỉ lấy xem trước, không lưu DB)
			ExternalMovieListResponse listResult = movieCrawlerService.fetchMovieList(source.getId(), 1);
			assertNotNull(listResult);
			assertTrue(listResult.isStatus());
			assertEquals(1, listResult.getItems().size());
			assertEquals("nhung-co-gai-de-thuong", listResult.getItems().get(0).getSlug());

			// 4. Kiểm thử crawlPage (lấy danh sách và tự động đồng bộ)
			List<String> successSlugs = movieCrawlerService.crawlPage(source.getId(), 1);
			assertEquals(1, successSlugs.size());
			assertEquals("nhung-co-gai-de-thuong", successSlugs.get(0));

			// Xác nhận phim đã được lưu vào DB
			var movieOpt = movieRepository.findBySlug("nhung-co-gai-de-thuong");
			assertTrue(movieOpt.isPresent());
			assertEquals("Những Cô Gái Dễ Thương", movieOpt.get().getName());

		} finally {
			// Khôi phục RestTemplate ban đầu
			ReflectionTestUtils.setField(movieCrawlerService, "restTemplate", originalRestTemplate);
			// Xóa nguồn phim test
			syncSourceRepository.delete(source);
		}
	}

}


