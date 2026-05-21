package com.phimhay.juanng.modules.catalog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sync_sources")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "list_url_pattern", nullable = false, length = 500)
    private String listUrlPattern;

    @Column(name = "detail_url_base", nullable = false, length = 500)
    private String detailUrlBase;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "last_synced_page")
    private Integer lastSyncedPage;

    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;
}
