package com.phimhay.juanng;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phimhay.juanng.modules.catalog.dto.ExternalMovieResponse;
import com.phimhay.juanng.modules.catalog.entity.Movie;
import com.phimhay.juanng.modules.catalog.repository.*;
import com.phimhay.juanng.modules.catalog.service.MovieSyncService;
import com.phimhay.juanng.modules.streaming.repository.EpisodeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JuanngApplicationTests {

	@Autowired
	private MovieSyncService movieSyncService;
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

}

