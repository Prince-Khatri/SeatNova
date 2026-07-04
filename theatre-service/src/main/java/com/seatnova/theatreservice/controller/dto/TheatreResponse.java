package com.seatnova.theatreservice.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TheatreResponse {
    private UUID id;
    private String name;
    private String city;
    private String address;
    private List<ScreenResponse> screens;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
