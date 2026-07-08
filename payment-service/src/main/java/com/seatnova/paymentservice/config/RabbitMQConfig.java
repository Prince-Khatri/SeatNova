package com.seatnova.paymentservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PAYMENT_EXCHANGE =
            "seatnova.payment.exchange";

    public static final String BOOKING_EXCHANGE =
            "seatnova.booking.exchange";

    public static final String BOOKING_CANCELLED_QUEUE =
            "payment.booking-cancelled.queue";

    public static final String BOOKING_CREATED_QUEUE =
            "payment.booking-created.queue";

    public static final String PAYMENT_SUCCEEDED_KEY =
            "payment.succeeded";

    public static final String PAYMENT_FAILED_KEY =
            "payment.failed";

    public static final String REFUND_COMPLETED_KEY =
            "refund.completed";

    @Bean
    public TopicExchange paymentExchange() {
        return ExchangeBuilder
                .topicExchange(PAYMENT_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public TopicExchange bookingExchange() {
        return ExchangeBuilder
                .topicExchange(BOOKING_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue bookingCancelledQueue() {
        return QueueBuilder
                .durable(BOOKING_CANCELLED_QUEUE)
                .build();
    }

    @Bean
    public Queue bookingCreatedQueue() {
        return QueueBuilder
                .durable(BOOKING_CREATED_QUEUE)
                .build();
    }

    @Bean
    public Binding bookingCancelledBinding() {
        return BindingBuilder
                .bind(bookingCancelledQueue())
                .to(bookingExchange())
                .with("booking.cancelled");
    }

    @Bean
    public Binding bookingCreatedBinding() {
        return BindingBuilder
                .bind(bookingCreatedQueue())
                .to(bookingExchange())
                .with("booking.created");
    }
}