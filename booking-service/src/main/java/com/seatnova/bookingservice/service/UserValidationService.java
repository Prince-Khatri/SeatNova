package com.seatnova.bookingservice.service;

import java.util.UUID;

public interface UserValidationService {
    boolean validateUserId(UUID userId);
}
