package com.phimhay.juanng.modules.interaction.entity;

import com.phimhay.juanng.common.utils.UlidHelper;
import com.phimhay.juanng.modules.streaming.entity.Episode;
import com.phimhay.juanng.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "watch_histories",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "episode_id"})}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchHistory {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @Column(name = "progress_seconds", nullable = false)
    @Builder.Default
    private int progressSeconds = 0;

    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private boolean isCompleted = false;

    @UpdateTimestamp
    @Column(name = "last_watched_at")
    private LocalDateTime lastWatchedAt;

    @PrePersist
    public void ensureId() {
        if (id == null) {
            id = UlidHelper.nextUlid();
        }
    }
}
