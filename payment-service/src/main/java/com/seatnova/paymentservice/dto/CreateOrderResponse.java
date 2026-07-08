package com.seatnova.paymentservice.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderResponse {

    private String message;

    private String razorpayOrderId;

    private BigDecimal amount;

    private String currency;

}
