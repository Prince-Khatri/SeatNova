package com.seatnova.paymentservice.dto;

import com.seatnova.paymentservice.entity.BookingPaymentStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private UUID id;

    private UUID userId;

    private BigDecimal totalAmount;

    private String bookingStatus;

    @Enumerated(EnumType.STRING)
    private BookingPaymentStatus paymentStatus;
}