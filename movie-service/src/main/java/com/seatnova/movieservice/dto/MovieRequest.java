package com.seatnova.movieservice.dto;

import com.seatnova.movieservice.entity.Genre;
import com.seatnova.movieservice.entity.Language;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieRequest {
    
    @NotBlank(message = "Title is required.")
    private String title;

    @NotBlank(message = "Description is required.")
    private String description;

    @Min(value = 1, message = "Duration must be greater than 0.")
    private Integer duration;

    @NotNull(message = "Release date is required.")
    private LocalDate releaseDate;

    @NotBlank(message = "Certificate is required.")
    private String certificate;

    private String posterUrl;
    private String trailerUrl;
    private Set<Genre> genres = new HashSet<>();
    private Set<Language> languages = new HashSet<>();
}