package com.seatnova.bookingservice.service.impl;

import com.seatnova.bookingservice.config.RabbitMQConfig;
import com.seatnova.bookingservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingEventConsumer {
    private final BookingService bookingService;

    @RabbitListener(queues = RabbitMQConfig.BOOKING_EXPIRY_QUEUE)
    public void processExpiryBooking(Map<String, Object> payload){
        UUID bookingId = UUID.fromString(payload.get("bookingId").toString());
        this.bookingService.expireBooking(bookingId);
    }


}
