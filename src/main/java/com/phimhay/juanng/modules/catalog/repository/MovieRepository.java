package com.phimhay.juanng.modules.catalog.repository;

import com.phimhay.juanng.modules.catalog.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String> {
    Optional<Movie> findByExternalId(Long externalId);
    Optional<Movie> findBySlug(String slug);
    boolean existsByExternalId(Long externalId);
    boolean existsBySlug(String slug);
}
