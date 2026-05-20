package com.phimhay.juanng.modules.streaming.entity;

import com.phimhay.juanng.common.utils.UlidHelper;
import com.phimhay.juanng.modules.catalog.entity.Movie;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "episodes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Episode {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(name = "server_name", nullable = false)
    private String serverName; // e.g. "Vietsub #1"

    @Column(nullable = false)
    private String name; // e.g. "Tập 1", "Full"

    @Column(nullable = false)
    private String slug; // e.g. "tap-1"

    @Column(name = "filename")
    private String filename;

    @Column(name = "link_embed", length = 1024)
    private String linkEmbed;

    @Column(name = "link_m3u8", length = 1024)
    private String linkM3u8;

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
