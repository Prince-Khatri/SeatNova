package com.seatnova.paymentservice.dto;

import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {

    @NotNull
    private UUID bookingId;

}
