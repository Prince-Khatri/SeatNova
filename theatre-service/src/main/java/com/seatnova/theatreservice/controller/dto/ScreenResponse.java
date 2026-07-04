package com.seatnova.theatreservice.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ScreenResponse {
    private UUID id;
    private UUID theatreId;
    private String name;
    private Integer totalSeats;
}