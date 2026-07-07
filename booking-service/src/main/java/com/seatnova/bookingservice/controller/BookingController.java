package com.seatnova.bookingservice.controller;


import com.seatnova.bookingservice.dto.BookingRequest;
import com.seatnova.bookingservice.dto.BookingResponse;
import com.seatnova.bookingservice.service.BookingService;
import com.seatnova.bookingservice.service.impl.BookingServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> reserveSeats(@Valid @RequestBody BookingRequest request){
        BookingResponse response = bookingService.reserveSeats(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

    }

    @GetMapping("{id}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.getBooking(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingResponse>> getUserBookings(@PathVariable UUID userId){
        return ResponseEntity.ok(
                this.bookingService
                        .getUserBooking(userId)
        );

    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable UUID id){
        ;
        return ResponseEntity.ok(this.bookingService.cancelBooking(id));
    }
}
