package com.seatnova.bookingservice.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
@Component
public interface BookingEventPublisher {
     void sendPaymentEvent(UUID bookingId, UUID userId, BigDecimal totalAmount);
     void sendPaymentCancelEvent(UUID bookingId, UUID userId, BigDecimal totalAmount);
     void sendBookingExpiryEvent(UUID bookingId, Duration delay);
}
