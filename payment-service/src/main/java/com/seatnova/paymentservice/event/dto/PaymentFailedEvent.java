package com.seatnova.paymentservice.event.dto;

import java.time.Instant;
import java.util.UUID;

public record PaymentFailedEvent(

        UUID eventId,

        UUID paymentId,

        UUID bookingId,

        UUID userId,

        String gatewayOrderId,

        String reason,

        Instant occurredAt
) {}