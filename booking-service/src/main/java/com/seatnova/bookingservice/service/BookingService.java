package com.seatnova.bookingservice.service;

import com.seatnova.bookingservice.dto.BookingRequest;
import com.seatnova.bookingservice.dto.BookingResponse;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface BookingService {
    @Transactional
    BookingResponse reserveSeats(@Valid BookingRequest request);

    BookingResponse getBooking(UUID id);

    List<BookingResponse> getUserBooking(UUID userId);

    @Transactional
    BookingResponse confirmBooking(UUID id);

    @Transactional
    BookingResponse releaseBooking(UUID id);

    @Transactional
    BookingResponse expireBooking(UUID id);

    @Transactional
    BookingResponse cancelBooking(UUID id);
}
