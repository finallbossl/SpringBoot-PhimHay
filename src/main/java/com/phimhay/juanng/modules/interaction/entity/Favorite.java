package com.phimhay.juanng.modules.interaction.entity;

import com.phimhay.juanng.common.utils.UlidHelper;
import com.phimhay.juanng.modules.catalog.entity.Movie;
import com.phimhay.juanng.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "favorites",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "movie_id"})}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void ensureId() {
        if (id == null) {
            id = UlidHelper.nextUlid();
        }
    }
}
