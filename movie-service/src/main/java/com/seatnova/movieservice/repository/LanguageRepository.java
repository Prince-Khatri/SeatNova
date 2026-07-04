package com.seatnova.movieservice.repository;

import com.seatnova.movieservice.entity.Genre;
import com.seatnova.movieservice.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LanguageRepository extends JpaRepository<Language, UUID> {
    Optional<Language> findByName(String name);
}
