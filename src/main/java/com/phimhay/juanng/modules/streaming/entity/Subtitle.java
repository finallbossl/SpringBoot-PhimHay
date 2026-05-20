package com.phimhay.juanng.modules.streaming.entity;

import com.phimhay.juanng.common.utils.UlidHelper;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "subtitles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subtitle {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @Column(name = "language_code", nullable = false, length = 10)
    private String languageCode; // e.g. "vi", "en", "ja"

    @Column(nullable = false)
    private String label; // e.g. "Tiếng Việt", "Tiếng Anh"

    @Column(name = "subtitle_url", nullable = false, length = 1024)
    private String subtitleUrl;

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
