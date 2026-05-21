package com.phimhay.juanng.modules.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalMovieResponse {
    private boolean status;
    private String msg;
    private MovieDto movie;
    private List<EpisodeServerDto> episodes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovieDto {
        private TmdbDto tmdb;
        private ImdbDto imdb;
        
        @JsonProperty("_id")
        private Long id;
        
        private String name;
        
        @JsonProperty("origin_name")
        private String originName;
        
        private String slug;
        private String content;
        private String type;
        private String status;
        
        @JsonProperty("poster_url")
        private String posterUrl;
        
        @JsonProperty("thumb_url")
        private String thumbUrl;
        
        @JsonProperty("is_copyright")
        private boolean isCopyright;
        
        @JsonProperty("trailer_url")
        private String trailerUrl;
        
        private String time;
        
        @JsonProperty("episode_current")
        private String episodeCurrent;
        
        @JsonProperty("episode_total")
        private String episodeTotal;
        
        private String quality;
        private String lang;
        private String showtimes;
        private int year;
        private int view;
        private boolean chieurap;
        
        @JsonProperty("sub_docquyen")
        private boolean subDocquyen;
        
        private List<String> actor;
        private List<String> director;
        private List<CategoryDto> category;
        private List<CountryDto> country;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TmdbDto {
        private String type;
        private String id;
        
        @JsonProperty("vote_average")
        private String voteAverage;
        
        @JsonProperty("vote_count")
        private Integer voteCount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImdbDto {
        private String id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDto {
        private Long id;
        private String name;
        private String slug;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CountryDto {
        private Long id;
        private String name;
        private String slug;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EpisodeServerDto {
        @JsonProperty("server_name")
        private String serverName;
        
        @JsonProperty("server_data")
        private List<EpisodeDataDto> serverData;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EpisodeDataDto {
        private String name;
        private String slug;
        private String filename;
        
        @JsonProperty("link_embed")
        private String linkEmbed;
        
        @JsonProperty("link_m3u8")
        private String linkM3u8;
    }
}
