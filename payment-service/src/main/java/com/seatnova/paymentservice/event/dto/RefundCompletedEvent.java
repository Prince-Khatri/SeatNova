package com.seatnova.paymentservice.event.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record RefundCompletedEvent(

        UUID eventId,

        UUID paymentId,

        UUID bookingId,

        UUID userId,

        BigDecimal amount,

        Instant occurredAt
) {}
