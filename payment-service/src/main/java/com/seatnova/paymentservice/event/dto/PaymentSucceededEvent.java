package com.seatnova.paymentservice.event.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentSucceededEvent(

        UUID eventId,

        UUID paymentId,

        UUID bookingId,

        UUID userId,

        String gatewayOrderId,

        String gatewayPaymentId,

        BigDecimal amount,

        String currency,

        Instant occurredAt
) {}