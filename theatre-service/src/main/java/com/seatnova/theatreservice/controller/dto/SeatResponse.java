package com.seatnova.theatreservice.controller.dto;

import com.seatnova.theatreservice.entity.SeatType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SeatResponse {
    private UUID id;
    private UUID screenId;
    private String rowLabel;
    private Integer seatNumber;
    private SeatType seatType;
}
