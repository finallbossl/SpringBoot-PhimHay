package com.phimhay.juanng.modules.catalog.repository;

import com.phimhay.juanng.modules.catalog.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActorRepository extends JpaRepository<Actor, String> {
    Optional<Actor> findBySlug(String slug);
    Optional<Actor> findByName(String name);
}
