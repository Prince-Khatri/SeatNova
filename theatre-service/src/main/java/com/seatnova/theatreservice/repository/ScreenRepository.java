package com.seatnova.theatreservice.repository;

import com.seatnova.theatreservice.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScreenRepository extends JpaRepository<Screen, UUID> {
}
