package com.seatnova.theatreservice.controller.dto;

import com.seatnova.theatreservice.entity.SeatType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SeatRequest {
    @NotBlank(message = "Row label is required")
    private String rowLabel;

    @NotNull(message = "Seat number is required")
    @Min(1)
    private Integer seatNumber;

    @NotNull(message = "Seat type is required")
    private SeatType seatType;
}
