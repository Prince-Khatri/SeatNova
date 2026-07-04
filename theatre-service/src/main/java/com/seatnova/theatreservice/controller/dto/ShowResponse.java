package com.seatnova.theatreservice.controller.dto;

import com.seatnova.theatreservice.entity.ShowStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ShowResponse {
    private UUID id;
    private UUID movieId;
    private UUID screenId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String language;
    private BigDecimal basePrice;
    private ShowStatus status;
}
