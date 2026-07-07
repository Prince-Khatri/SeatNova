package com.seatnova.bookingservice.service;

import java.util.UUID;

public interface TheatreValidationService {
    boolean validateSeatId(UUID seatId);

    boolean validateShowId(UUID seatId);
}
