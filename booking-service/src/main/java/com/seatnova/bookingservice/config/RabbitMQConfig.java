package com.seatnova.bookingservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.Topic;

@Configuration
public class RabbitMQConfig {

    // Exchanges
    public static final String BOOKING_EXCHANGE = "seatnova.booking.exchange";
    public static final String BOOKING_EXPIRY_EXCHANGE = "seatnova.booking.expiry.exchange";
    public static final String BOOKING_DELAY_EXCHANGE = "seatnova.booking.delay.exchange";
    public static final String PAYMENT_EXCHANGE = "seatnova.payment.exchange";

    // Queues (owned by Booking Service)
    public static final String PAYMENT_SUCCEEDED_QUEUE = "booking.payment-succeeded.queue";
    public static final String PAYMENT_FAILED_QUEUE = "booking.payment-failed.queue";
    public static final String BOOKING_EXPIRY_QUEUE = "seatnova.booking.expiry.queue";
    public static final String BOOKING_DELAY_QUEUE = "seatnova.booking.delay.queue";

    // Booking Events
    public static final String BOOKING_CREATED_KEY = "booking.created";
    public static final String BOOKING_CANCELLED_KEY = "booking.cancelled";
    public static final String BOOKING_EXPIRY_KEY = "booking.expiry";
    public static final String BOOKING_DELAY_KEY = "booking.expiry.delay";

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
    public TopicExchange bookingExpiryExchange(){
        return ExchangeBuilder
                .topicExchange(BOOKING_EXPIRY_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue bookingExpiryQueue(){
        return QueueBuilder
                .durable(BOOKING_EXPIRY_QUEUE)
                .build();
    }

    @Bean
    public Binding bookingExpiryBinder(Queue bookingExpiryQueue, TopicExchange bookingExpiryExchange){
        return BindingBuilder
                .bind(bookingExpiryQueue)
                .to(bookingExpiryExchange)
                .with(BOOKING_EXPIRY_KEY);
    }

    @Bean
    public TopicExchange bookingDelayExchange(){
        return ExchangeBuilder
                .topicExchange(BOOKING_DELAY_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue bookingDelayQueue(){
        return QueueBuilder
                .durable(BOOKING_DELAY_QUEUE)
                .deadLetterExchange(BOOKING_EXPIRY_EXCHANGE)
                .deadLetterRoutingKey(BOOKING_EXPIRY_KEY)
                .build();
    }

    @Bean Binding bookingDealyBinding(Queue bookingDelayQueue, TopicExchange bookingDelayExchange){
        return BindingBuilder
                .bind(bookingDelayQueue)
                .to(bookingDelayExchange)
                .with(BOOKING_DELAY_KEY);
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