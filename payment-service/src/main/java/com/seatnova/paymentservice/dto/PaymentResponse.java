package com.seatnova.paymentservice.dto;

import com.seatnova.paymentservice.entity.PaymentMethod;
import com.seatnova.paymentservice.entity.BookingPaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private String message;

    private UUID bookingId;

    private String razorpayOrderId;

    private String razorpayPaymentId;

    private BigDecimal amount;

    private String currency;

    private BookingPaymentStatus status;

    private PaymentMethod method;

}
