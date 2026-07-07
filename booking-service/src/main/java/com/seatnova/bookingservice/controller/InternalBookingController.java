package com.seatnova.bookingservice.controller;

import com.seatnova.bookingservice.dto.BookingResponse;
import com.seatnova.bookingservice.service.BookingService;
import com.seatnova.bookingservice.service.impl.BookingServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/bookings")
@RequiredArgsConstructor
public class InternalBookingController {
    private final BookingService bookingService;

    @PostMapping("/{id}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(@PathVariable UUID id){
        return ResponseEntity.ok(this.bookingService.confirmBooking(id));
    }


    @PostMapping("/{id}/expire")
    public ResponseEntity<BookingResponse> expireBooking(@PathVariable UUID id){
        return ResponseEntity.ok(this.bookingService.expireBooking(id));
    }
    @PostMapping("/{id}/release")
    public ResponseEntity<BookingResponse> releaseBooking(@PathVariable UUID id){
        return ResponseEntity.ok(this.bookingService.releaseBooking(id));
    }
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable UUID id){
        return ResponseEntity.ok(this.bookingService.getBooking(id));
    }

}
