package com.phimhay.juanng.modules.catalog.entity;

import com.phimhay.juanng.common.utils.UlidHelper;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "directors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Director {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(name = "avatar_url", length = 512)
    private String avatarUrl;

    @PrePersist
    public void ensureId() {
        if (id == null) {
            id = UlidHelper.nextUlid();
        }
    }
}
