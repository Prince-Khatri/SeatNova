package com.seatnova.bookingservice.service;

import com.seatnova.bookingservice.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.util.Map;

public interface PaymentResultConsumer {
    @RabbitListener(queues = RabbitMQConfig.PAYMENT_SUCCEEDED_QUEUE)
    void processPaymentSuccess(Map<String, Object> payload);

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_FAILED_QUEUE)
    void processPaymentFailure(Map<String, Object> payload);
}
