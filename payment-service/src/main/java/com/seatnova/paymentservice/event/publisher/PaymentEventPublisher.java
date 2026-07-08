package com.seatnova.paymentservice.event.publisher;

import com.seatnova.paymentservice.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishPaymentSucceeded(Object event) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_SUCCEEDED_KEY,
                event
        );
    }

    public void publishPaymentFailed(Object event) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.PAYMENT_FAILED_KEY,
                event
        );
    }

    public void publishRefundCompleted(Object event) {

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PAYMENT_EXCHANGE,
                RabbitMQConfig.REFUND_COMPLETED_KEY,
                event
        );
    }
}