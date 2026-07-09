package com.seatnova.bookingservice.entity;

import com.seatnova.bookingservice.dto.BookingRequest;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="bookings")
@NoArgsConstructor
@Data

public class Booking {
    public Booking(BookingRequest request){
        this.userId = request.getUserId();
        this.showId = request.getShowId();
        this.totalAmount = request.getTotalAmount();
    }
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable=false)
    private UUID userId;

    @Column(nullable=false)
    private UUID showId;

    @Column(nullable = false, precision=10, scale=2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private BookingStatus bookingStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @OneToMany(
            mappedBy = "booking",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    List<BookingSeat> bookingSeats;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;


}
