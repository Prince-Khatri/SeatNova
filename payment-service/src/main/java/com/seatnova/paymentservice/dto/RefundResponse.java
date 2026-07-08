package com.seatnova.paymentservice.dto;

import com.seatnova.paymentservice.entity.BookingPaymentStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponse {

    private String message;

    private UUID bookingId;

    private UUID paymentId;

    private BookingPaymentStatus status;
}
