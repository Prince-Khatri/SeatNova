package com.seatnova.theatreservice.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ScreenRequest {
    @NotBlank(message = "Screen name is required")
    private String name;

    @NotNull(message = "Total seats must be specified")
    @Min(1)
    private Integer totalSeats;

    @NotNull(message = "Theatre ID is required")
    private UUID theatreId;
}