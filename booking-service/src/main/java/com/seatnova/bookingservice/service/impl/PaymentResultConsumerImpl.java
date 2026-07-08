package com.seatnova.bookingservice.service.impl;

import com.seatnova.bookingservice.config.RabbitMQConfig;
import com.seatnova.bookingservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentResultConsumerImpl implements com.seatnova.bookingservice.service.PaymentResultConsumer {
    private final BookingService bookingService;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_SUCCEEDED_QUEUE)
    @Override
    public void processPaymentSuccess(Map<String, Object> payload) {
        try {
            String bookingIdStr = (String) payload.get("bookingId");
            UUID bookingId = UUID.fromString(bookingIdStr);
            
            bookingService.confirmBooking(bookingId); // HOLD-> CONFIRM
        } catch (AmqpException e) {
            System.err.println("Error mapping inbound stream conversion processing: " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_FAILED_QUEUE)
    @Override
    public void processPaymentFailure(Map<String, Object> payload) {
        try {
            String bookingIdStr = (String) payload.get("bookingId");
            UUID bookingId = UUID.fromString(bookingIdStr);
            bookingService.releaseBooking(bookingId); // HOLD-> Exipred
        } catch (AmqpException e) {
            System.err.println("Error mapping inbound stream conversion processing: " + e.getMessage());
        }
    }


}
