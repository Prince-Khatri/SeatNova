package com.seatnova.theatreservice.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TheatreRequest {
    @NotBlank(message = "Theatre name is mandatory")
    private String name;
    private String city;
    private String address;
}
