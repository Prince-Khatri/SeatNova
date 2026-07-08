package com.seatnova.paymentservice.dto;

import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest {

    @NotNull
    private UUID bookingId;

}
