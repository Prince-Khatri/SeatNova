package com.seatnova.bookingservice.service;

import java.util.List;
import java.util.UUID;

public interface SeatLockService {

    boolean lockSeats(UUID showId, List<UUID> seatIds);

    void releaseSeats(UUID showId, List<UUID> seatIds);

    void confirmSeats(UUID showId, List<UUID> seatIds);

}