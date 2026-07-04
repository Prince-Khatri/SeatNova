package com.seatnova.movieservice.service;

import com.seatnova.movieservice.entity.Genre;
import com.seatnova.movieservice.entity.Language;
import com.seatnova.movieservice.repository.GenreRepository;
import com.seatnova.movieservice.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class LanguageService {
    private final LanguageRepository languageRepository;

    public Set<Language> saveLanguage(Set<Language> languages){
        Set<Language> updated = new HashSet<>();

        for(Language l: languages){
            assert this.languageRepository != null;
            updated.add(this.languageRepository.findByName(l.getName()).orElseGet(()-> languageRepository.save(l)));
        }
        return updated;
    }



}
