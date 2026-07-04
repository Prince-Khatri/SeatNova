package com.seatnova.movieservice.service;

import com.seatnova.movieservice.dto.MovieRequest;
import com.seatnova.movieservice.dto.MovieResponse;
import com.seatnova.movieservice.entity.Movie;
import com.seatnova.movieservice.entity.MovieStatus;
import com.seatnova.movieservice.repository.MovieRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MovieService {
    private final MovieRepository movieRepository;
    private final GenreService genreService;
    private final LanguageService languageService;

    public MovieResponse createMovie(@Valid MovieRequest movieRequest) {
        // preparing if the genres or languages doesn't exist.
        movieRequest.setGenres(this.genreService.saveGenre(movieRequest.getGenres()));
        movieRequest.setLanguages(this.languageService.saveLanguage(movieRequest.getLanguages()));

        Movie movie = new Movie(movieRequest);

        return new MovieResponse(movieRepository.save(movie));
    }

    public MovieResponse updateMovie(UUID id, @Valid MovieRequest movieRequest) {
        // preparing if the genres or languages doesn't exist.
        movieRequest.setGenres(this.genreService.saveGenre(movieRequest.getGenres()));
        movieRequest.setLanguages(this.languageService.saveLanguage(movieRequest.getLanguages()));

        Movie movie = this.movieRepository.findFirstById(id);
        if(movie==null) return null;

        return new MovieResponse(this.movieRepository.save(new Movie(movieRequest)));
    }

    public void deleteMovie(UUID id) {
        Movie movie = this.movieRepository.findFirstById(id);
        if(movie==null) return;
        this.movieRepository.delete(movie);
    }

    public MovieResponse updateStatus(UUID id, MovieStatus status) {
        Movie movie = this.movieRepository.findFirstById(id);
        if(movie==null) return null;
        movie.setStatus(status);
        return new MovieResponse(movieRepository.save(movie));
    }

    public List<MovieResponse> getMovies(MovieStatus status, String language, String genres) {
        List<Movie> movies = this.movieRepository.findAllByStatusAndLanguagesAndGenres(status, language, genres);
        System.out.println("hello");
        for (Movie m:movies)
            System.out.println(m);
        return this.prepareResponse(movies);
    }

    private @NonNull List<MovieResponse> prepareResponse(List<Movie> movies) {
        List<MovieResponse> movieResponsesList=new ArrayList<>();
        for(Movie m:movies) {
            movieResponsesList.add(new MovieResponse(m));
        }
        return movieResponsesList;
    }

    public MovieResponse getMovie(UUID id) {
        return new MovieResponse(this.movieRepository.findFirstById(id));
    }

    public List<MovieResponse> searchMovie(String title) {
        System.out.println(title);
        return prepareResponse(this.movieRepository.findAllByTitleLikeIgnoreCase("%"+title+"%"));
    }
}
