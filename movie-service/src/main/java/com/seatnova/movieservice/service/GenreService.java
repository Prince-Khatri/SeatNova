package com.seatnova.movieservice.service;

import com.seatnova.movieservice.entity.Genre;
import com.seatnova.movieservice.repository.GenreRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public Set<Genre> saveGenre(Set<Genre> genres){
        Set<Genre> updated = new HashSet<>();

        for(Genre g: genres){
            assert this.genreRepository != null;
            updated.add(this.genreRepository.findByName(g.getName()).orElseGet(()-> genreRepository.save(g)));
        }
        return updated;
    }



}
