package com.seatnova.paymentservice.client;

import com.seatnova.paymentservice.dto.BookingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "booking-service")
public interface BookingClient {

    @GetMapping("/internal/bookings/{bookingId}")
    BookingResponse getBooking(@PathVariable UUID bookingId);
}