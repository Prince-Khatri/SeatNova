package com.seatnova.bookingservice.service.impl;

import com.seatnova.bookingservice.config.RabbitMQConfig;
import com.seatnova.bookingservice.service.BookingEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingEventPublisherImpl implements BookingEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    @Override
    public void sendPaymentEvent(UUID bookingId, UUID userId, BigDecimal totalAmount) {
        this.createPayloadAndSend(
                bookingId,
                userId,
                totalAmount,
                RabbitMQConfig.BOOKING_EXCHANGE,
                RabbitMQConfig.BOOKING_CREATED_KEY
        );

    }

    @Override
    public void sendPaymentCancelEvent(UUID bookingId, UUID userId, BigDecimal totalAmount) {
        this.createPayloadAndSend(
                bookingId,
                userId,
                totalAmount,
                RabbitMQConfig.BOOKING_EXCHANGE,
                RabbitMQConfig.BOOKING_CANCELLED_KEY
        );
    }

    @Override
    public void sendBookingExpiryEvent(UUID bookingId, Duration delay) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("bookingId", bookingId);

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.BOOKING_DELAY_EXCHANGE,
                RabbitMQConfig.BOOKING_DELAY_KEY,
                payload,
                message->{
                    message
                        .getMessageProperties()
                        .setExpiration(
                            String.valueOf(
                                delay.toMillis()
                            )
                        );
                    return message;
                }
        );

    }


    private void createPayloadAndSend(UUID bookingId, UUID userId, BigDecimal totalAmount, String exchange, String key) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("bookingId", bookingId);
        payload.put("userId", userId);
        payload.put("totalAmount", totalAmount);
        rabbitTemplate.convertAndSend(
                exchange,
                key,
                payload
        );
    }
}
