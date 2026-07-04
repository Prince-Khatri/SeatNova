package com.seatnova.theatreservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.LongStream;

@Entity
@Table(name = "shows")
@Getter @Setter @NoArgsConstructor
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID movieId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String language;
    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    private ShowStatus status;


}
