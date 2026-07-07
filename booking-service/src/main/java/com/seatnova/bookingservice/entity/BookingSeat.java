package com.seatnova.bookingservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name="booking_seat",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"booking_id", "seat_id"}
                )
        }
)
@NoArgsConstructor
@Data
public class BookingSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "seat_id", nullable = false)
    private UUID seatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    
}
