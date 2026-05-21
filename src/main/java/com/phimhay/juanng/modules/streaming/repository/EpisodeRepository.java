package com.phimhay.juanng.modules.streaming.repository;

import com.phimhay.juanng.modules.streaming.entity.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, String> {
    Optional<Episode> findByMovieIdAndServerNameAndSlug(String movieId, String serverName, String slug);
    List<Episode> findByMovieId(String movieId);
    void deleteByMovieId(String movieId);
}
