package com.seatnova.movieservice.controller;

import com.seatnova.movieservice.dto.MovieRequest;
import com.seatnova.movieservice.dto.MovieResponse;
import com.seatnova.movieservice.entity.MovieStatus;
import com.seatnova.movieservice.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;

    @PostMapping
    public ResponseEntity<MovieResponse> createMovie(@Valid @RequestBody MovieRequest movieRequest){
        return ResponseEntity.ok(movieService.createMovie(movieRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovieResponse> updateMovie(@PathVariable UUID id, @Valid @RequestBody MovieRequest movieRequest){

        return ResponseEntity.ok(movieService.updateMovie(id, movieRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovie(@PathVariable UUID id){
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}/status")
    public ResponseEntity<MovieResponse> updateStatus(
            @PathVariable UUID id,
            @RequestParam MovieStatus status
    ){

        return ResponseEntity.ok(movieService.updateStatus(id,status));
    }

    @GetMapping
    public ResponseEntity<List<MovieResponse>> getMovies(
            @RequestParam(required = false) MovieStatus status,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String genre
    ){
        return ResponseEntity.ok(movieService.getMovies(status, language, genre));

    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovie(@PathVariable UUID id) {
        return ResponseEntity.ok(movieService.getMovie(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovieResponse>> searchMovie(
            @RequestParam String title) {
        return ResponseEntity.ok(movieService.searchMovie(title));
    }



}
