package com.phimhay.juanng.modules.catalog.entity;

import com.phimhay.juanng.common.utils.UlidHelper;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {
    @Id
    private String id;

    @Column(name = "external_id", unique = true)
    private Long externalId; // _id từ OPhim/KKPhim (ví dụ: 48298)

    @Column(nullable = false)
    private String name;

    @Column(name = "origin_name")
    private String originName;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String type; // single (phim lẻ), series (phim bộ), hoathinh, tvshows

    private String status; // trailer, ongoing, completed

    @Column(name = "poster_url", length = 512)
    private String posterUrl;

    @Column(name = "thumb_url", length = 512)
    private String thumbUrl;

    @Column(name = "is_copyright")
    private boolean isCopyright;

    @Column(name = "trailer_url", length = 512)
    private String trailerUrl;

    private String time; // Thời lượng phim (ví dụ: "106")

    @Column(name = "episode_current")
    private String episodeCurrent; // Tập hiện tại (ví dụ: "Trailer", "Tập 1")

    @Column(name = "episode_total")
    private String episodeTotal; // Tổng số tập (ví dụ: "1")

    private String quality; // HD, Full HD, 4K

    private String lang; // Vietsub, Thuyết minh

    private String showtimes;

    private int year;

    @Builder.Default
    private int view = 0;

    @Builder.Default
    private boolean chieurap = false;

    @Column(name = "sub_docquyen")
    @Builder.Default
    private boolean subDocquyen = false;

    // TMDB metadata
    @Column(name = "tmdb_id")
    private String tmdbId;

    @Column(name = "tmdb_type")
    private String tmdbType;

    @Column(name = "tmdb_vote_average")
    private Double tmdbVoteAverage;

    @Column(name = "tmdb_vote_count")
    private Integer tmdbVoteCount;

    // IMDB metadata
    @Column(name = "imdb_id")
    private String imdbId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_categories",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_countries",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "country_id")
    )
    @Builder.Default
    private Set<Country> countries = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_actors",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    @Builder.Default
    private Set<Actor> actors = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_directors",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "director_id")
    )
    @Builder.Default
    private Set<Director> directors = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void ensureId() {
        if (id == null) {
            id = UlidHelper.nextUlid();
        }
    }
}
