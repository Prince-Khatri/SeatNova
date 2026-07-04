package com.seatnova.movieservice.entity;

import com.seatnova.movieservice.dto.MovieRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
@Entity
@Table(name = "movies")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private LocalDate releaseDate;

    private String certificate;

    private String posterUrl;

    private String trailerUrl;

    private MovieStatus status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_languages",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    private Set<Language> languages = new HashSet<>();

    public Movie(MovieRequest movieRequest){
        this.title = movieRequest.getTitle();
        this.description = movieRequest.getDescription();
        this.duration=movieRequest.getDuration();
        this.releaseDate = movieRequest.getReleaseDate();
        this.certificate = movieRequest.getCertificate();
        this.posterUrl = movieRequest.getPosterUrl();
        this.trailerUrl = movieRequest.getTrailerUrl();
        this.status=MovieStatus.UPCOMING;
        this.languages = movieRequest.getLanguages();
        this.genres = movieRequest.getGenres();
    }
}