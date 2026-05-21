package com.phimhay.juanng.modules.catalog.repository;

import com.phimhay.juanng.modules.catalog.entity.SyncSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SyncSourceRepository extends JpaRepository<SyncSource, Long> {
    Optional<SyncSource> findByName(String name);
}
