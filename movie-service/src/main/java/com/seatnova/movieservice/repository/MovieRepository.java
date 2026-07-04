package com.seatnova.movieservice.repository;

import com.seatnova.movieservice.entity.Movie;
import com.seatnova.movieservice.entity.MovieStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {
    Movie findFirstById(UUID id);

    @Query("""
            SELECT DISTINCT m
            FROM Movie m
            LEFT JOIN m.languages l
            LEFT JOIN m.genres g
            WHERE (:status IS NULL OR m.status = :status)
            AND (:languages IS NULL OR l.name = :languages)
            AND (:genres IS NULL OR g.name = :genres)
    """)
    List<Movie> findAllByStatusAndLanguagesAndGenres(MovieStatus status, String languages, String genres);

    List<Movie> findAllByTitleLikeIgnoreCase(String title);
}
