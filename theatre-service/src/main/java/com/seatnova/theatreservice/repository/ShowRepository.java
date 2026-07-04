package com.seatnova.theatreservice.repository;

import com.seatnova.theatreservice.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShowRepository extends JpaRepository<Show, UUID> {
    List<Show> findByScreen_Theatre_City(String city);

    List<Show> findByMovieId(UUID movieId);

    List<Show> findByScreenId(UUID id);
}
