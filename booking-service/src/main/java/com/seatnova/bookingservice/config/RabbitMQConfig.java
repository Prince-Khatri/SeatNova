package com.seatnova.bookingservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchanges
    public static final String BOOKING_EXCHANGE = "seatnova.booking.exchange";
    public static final String PAYMENT_EXCHANGE = "seatnova.payment.exchange";

    // Queues (owned by Booking Service)
    public static final String PAYMENT_SUCCEEDED_QUEUE = "booking.payment-succeeded.queue";
    public static final String PAYMENT_FAILED_QUEUE = "booking.payment-failed.queue";

    // Booking Events
    public static final String BOOKING_CREATED_KEY = "booking.created";
    public static final String BOOKING_CANCELLED_KEY = "booking.cancelled";

    // Payment Events
    public static final String PAYMENT_SUCCEEDED_KEY = "payment.succeeded";
    public static final String PAYMENT_FAILED_KEY = "payment.failed";

    @Bean
    public TopicExchange bookingExchange() {
        return ExchangeBuilder
                .topicExchange(BOOKING_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public TopicExchange paymentExchange() {
        return ExchangeBuilder
                .topicExchange(PAYMENT_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue paymentSucceededQueue() {
        return QueueBuilder
                .durable(PAYMENT_SUCCEEDED_QUEUE)
                .build();
    }

    @Bean
    public Queue paymentFailedQueue() {
        return QueueBuilder
                .durable(PAYMENT_FAILED_QUEUE)
                .build();
    }

    @Bean
    public Binding paymentSucceededBinding(
            Queue paymentSucceededQueue,
            TopicExchange paymentExchange) {

        return BindingBuilder.bind(paymentSucceededQueue)
                .to(paymentExchange)
                .with(PAYMENT_SUCCEEDED_KEY);
    }

    @Bean
    public Binding paymentFailedBinding(
            Queue paymentFailedQueue,
            TopicExchange paymentExchange) {

        return BindingBuilder.bind(paymentFailedQueue)
                .to(paymentExchange)
                .with(PAYMENT_FAILED_KEY);
    }
}