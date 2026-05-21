package com.phimhay.juanng.modules.catalog.repository;

import com.phimhay.juanng.modules.catalog.entity.Director;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DirectorRepository extends JpaRepository<Director, String> {
    Optional<Director> findBySlug(String slug);
    Optional<Director> findByName(String name);
}
