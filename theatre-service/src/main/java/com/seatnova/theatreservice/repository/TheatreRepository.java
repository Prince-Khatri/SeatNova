package com.seatnova.theatreservice.repository;

import com.seatnova.theatreservice.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TheatreRepository extends JpaRepository<Theatre, UUID> {
}
