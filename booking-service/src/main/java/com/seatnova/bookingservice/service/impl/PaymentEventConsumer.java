package com.seatnova.bookingservice.service.impl;

import com.seatnova.bookingservice.config.RabbitMQConfig;
import com.seatnova.bookingservice.service.BookingService;
import com.seatnova.bookingservice.service.SeatLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {
    private final BookingService bookingService;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_SUCCEEDED_QUEUE)
    public void processPaymentSuccess(Map<String, Object> payload) {
        try {
            String bookingIdStr = (String) payload.get("bookingId");
            UUID bookingId = UUID.fromString(bookingIdStr);


            bookingService.confirmBooking(bookingId); // HOLD-> CONFIRM
        } catch (AmqpException e) {
            System.err.println("Error mapping inbound stream conversion processing: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_FAILED_QUEUE)
    public void processPaymentFailure(Map<String, Object> payload) {
        try {
            String bookingIdStr = (String) payload.get("bookingId");
            UUID bookingId = UUID.fromString(bookingIdStr);
            bookingService.releaseBooking(bookingId); // HOLD-> Exipred
        } catch (AmqpException e) {
            System.err.println("Error mapping inbound stream conversion processing: " + e.getMessage());
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }


}
