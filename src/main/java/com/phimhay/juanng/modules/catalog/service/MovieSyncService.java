package com.phimhay.juanng.modules.catalog.service;

import com.phimhay.juanng.common.exception.AppException;
import com.phimhay.juanng.common.exception.ResultCode;
import com.phimhay.juanng.common.utils.SlugHelper;
import com.phimhay.juanng.modules.catalog.dto.ExternalMovieResponse;
import com.phimhay.juanng.modules.catalog.entity.*;
import com.phimhay.juanng.modules.catalog.repository.*;
import com.phimhay.juanng.modules.streaming.entity.Episode;
import com.phimhay.juanng.modules.streaming.repository.EpisodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieSyncService {

    private final MovieRepository movieRepository;
    private final CategoryRepository categoryRepository;
    private final CountryRepository countryRepository;
    private final ActorRepository actorRepository;
    private final DirectorRepository directorRepository;
    private final EpisodeRepository episodeRepository;

    @Transactional
    public Movie syncMovie(ExternalMovieResponse response) {
        if (response == null || !response.isStatus() || response.getMovie() == null) {
            throw new AppException(ResultCode.INVALID_INPUT);
        }

        ExternalMovieResponse.MovieDto movieDto = response.getMovie();
        log.info("Bắt đầu đồng bộ phim: {} (ID ngoài: {})", movieDto.getName(), movieDto.getId());

        // 1. Tìm hoặc tạo mới Movie
        Movie movie = movieRepository.findByExternalId(movieDto.getId())
                .orElseGet(() -> movieRepository.findBySlug(movieDto.getSlug())
                        .orElse(new Movie()));

        // 2. Thiết lập/Cập nhật các thông tin cơ bản
        movie.setExternalId(movieDto.getId());
        movie.setName(movieDto.getName());
        movie.setOriginName(movieDto.getOriginName());
        movie.setSlug(movieDto.getSlug());
        movie.setContent(movieDto.getContent());
        movie.setType(movieDto.getType());
        movie.setStatus(movieDto.getStatus());
        movie.setPosterUrl(movieDto.getPosterUrl());
        movie.setThumbUrl(movieDto.getThumbUrl());
        movie.setCopyright(movieDto.isCopyright());
        movie.setTrailerUrl(movieDto.getTrailerUrl());
        movie.setTime(movieDto.getTime());
        movie.setEpisodeCurrent(movieDto.getEpisodeCurrent());
        movie.setEpisodeTotal(movieDto.getEpisodeTotal());
        movie.setQuality(movieDto.getQuality());
        movie.setLang(movieDto.getLang());
        movie.setShowtimes(movieDto.getShowtimes());
        movie.setYear(movieDto.getYear());
        movie.setSubDocquyen(movieDto.isSubDocquyen());
        movie.setChieurap(movieDto.isChieurap());

        // Đồng bộ TMDB / IMDB
        if (movieDto.getTmdb() != null) {
            movie.setTmdbId(movieDto.getTmdb().getId());
            movie.setTmdbType(movieDto.getTmdb().getType());
            movie.setTmdbVoteCount(movieDto.getTmdb().getVoteCount());
            
            String avg = movieDto.getTmdb().getVoteAverage();
            if (avg != null && !avg.trim().isEmpty()) {
                try {
                    movie.setTmdbVoteAverage(Double.parseDouble(avg));
                } catch (NumberFormatException e) {
                    log.warn("Lỗi parse điểm vote TMDB: {}", avg);
                    movie.setTmdbVoteAverage(0.0);
                }
            }
        }
        
        if (movieDto.getImdb() != null) {
            movie.setImdbId(movieDto.getImdb().getId());
        }

        // 3. Đồng bộ Category (Thể loại)
        Set<Category> categories = new HashSet<>();
        if (movieDto.getCategory() != null) {
            for (ExternalMovieResponse.CategoryDto catDto : movieDto.getCategory()) {
                Category category = categoryRepository.findBySlug(catDto.getSlug())
                        .orElseGet(() -> categoryRepository.save(
                                Category.builder()
                                        .name(catDto.getName())
                                        .slug(catDto.getSlug())
                                        .build()
                        ));
                categories.add(category);
            }
        }
        movie.setCategories(categories);

        // 4. Đồng bộ Country (Quốc gia)
        Set<Country> countries = new HashSet<>();
        if (movieDto.getCountry() != null) {
            for (ExternalMovieResponse.CountryDto countryDto : movieDto.getCountry()) {
                Country country = countryRepository.findBySlug(countryDto.getSlug())
                        .orElseGet(() -> countryRepository.save(
                                Country.builder()
                                        .name(countryDto.getName())
                                        .slug(countryDto.getSlug())
                                        .build()
                        ));
                countries.add(country);
            }
        }
        movie.setCountries(countries);

        // 5. Đồng bộ Actor (Diễn viên)
        Set<Actor> actors = new HashSet<>();
        if (movieDto.getActor() != null) {
            for (String actorName : movieDto.getActor()) {
                if (actorName == null || actorName.trim().isEmpty()) continue;
                String slug = SlugHelper.toSlug(actorName);
                if (slug.isEmpty()) continue;

                Actor actor = actorRepository.findBySlug(slug)
                        .orElseGet(() -> actorRepository.save(
                                Actor.builder()
                                        .name(actorName)
                                        .slug(slug)
                                        .build()
                        ));
                actors.add(actor);
            }
        }
        movie.setActors(actors);

        // 6. Đồng bộ Director (Đạo diễn)
        Set<Director> directors = new HashSet<>();
        if (movieDto.getDirector() != null) {
            for (String directorName : movieDto.getDirector()) {
                if (directorName == null || directorName.trim().isEmpty()) continue;
                String slug = SlugHelper.toSlug(directorName);
                if (slug.isEmpty()) continue;

                Director director = directorRepository.findBySlug(slug)
                        .orElseGet(() -> directorRepository.save(
                                Director.builder()
                                        .name(directorName)
                                        .slug(slug)
                                        .build()
                        ));
                directors.add(director);
            }
        }
        movie.setDirectors(directors);

        // 7. Lưu phim vào DB để sinh ID (nếu tạo mới)
        Movie savedMovie = movieRepository.save(movie);

        // 8. Đồng bộ Episodes (Tập phim) - Cập nhật đè (Option 1)
        if (response.getEpisodes() != null) {
            for (ExternalMovieResponse.EpisodeServerDto serverDto : response.getEpisodes()) {
                String serverName = serverDto.getServerName();
                if (serverDto.getServerData() != null) {
                    for (ExternalMovieResponse.EpisodeDataDto epData : serverDto.getServerData()) {
                        // Tìm tập phim đã tồn tại theo server_name và slug của tập
                        Episode episode = episodeRepository.findByMovieIdAndServerNameAndSlug(
                                savedMovie.getId(), serverName, epData.getSlug())
                                .orElseGet(() -> Episode.builder()
                                        .movie(savedMovie)
                                        .serverName(serverName)
                                        .slug(epData.getSlug())
                                        .build());

                        episode.setName(epData.getName());
                        episode.setFilename(epData.getFilename());
                        episode.setLinkEmbed(epData.getLinkEmbed());
                        episode.setLinkM3u8(epData.getLinkM3u8());

                        episodeRepository.save(episode);
                    }
                }
            }
        }

        log.info("Đồng bộ phim thành công: {}", savedMovie.getName());
        return savedMovie;
    }
}
