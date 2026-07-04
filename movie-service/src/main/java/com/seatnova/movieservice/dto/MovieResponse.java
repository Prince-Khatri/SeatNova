package com.seatnova.movieservice.dto;

import com.seatnova.movieservice.entity.Genre;
import com.seatnova.movieservice.entity.Language;
import com.seatnova.movieservice.entity.Movie;
import com.seatnova.movieservice.entity.MovieStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MovieResponse {
    private UUID id;
    private String title;
    private String description;
    private Integer duration;
    private LocalDate releaseDate;
    private String certificate;
    private String posterUrl;
    private String trailerUrl;
    private MovieStatus status;
    private Set<Genre> genres = new HashSet<>();
    private Set<Language> languages = new HashSet<>();

    public MovieResponse(Movie movie) {
            this.id = movie.getId();
            this.title = movie.getTitle();
            this.description = movie.getDescription();
            this.duration=movie.getDuration();
            this.releaseDate = movie.getReleaseDate();
            this.certificate = movie.getCertificate();
            this.posterUrl = movie.getPosterUrl();
            this.trailerUrl = movie.getTrailerUrl();
            this.status=movie.getStatus();
            this.languages = movie.getLanguages();
            this.genres = movie.getGenres();
    }
}
