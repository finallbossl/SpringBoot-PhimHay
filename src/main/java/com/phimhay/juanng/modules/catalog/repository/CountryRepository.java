package com.phimhay.juanng.modules.catalog.repository;

import com.phimhay.juanng.modules.catalog.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findBySlug(String slug);
    Optional<Country> findByName(String name);
}
