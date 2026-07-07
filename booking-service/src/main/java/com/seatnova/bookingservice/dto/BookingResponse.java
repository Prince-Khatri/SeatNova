package com.seatnova.bookingservice.dto;

import com.seatnova.bookingservice.entity.Booking;
import com.seatnova.bookingservice.entity.BookingStatus;
import com.seatnova.bookingservice.entity.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class BookingResponse {
    private UUID id;
    private UUID userId;
    private UUID showId;
    private BigDecimal totalAmount;
    private BookingStatus bookingStatus;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<UUID> seatIds;

    public BookingResponse(Booking booking) {
        this.id = booking.getId();
        this.userId = booking.getUserId();
        this.showId = booking.getShowId();
        this.totalAmount = booking.getTotalAmount();
        this.bookingStatus = booking.getBookingStatus();
        this.paymentStatus = booking.getPaymentStatus();
        this.createdAt = booking.getCreatedAt();
        this.updatedAt = booking.getUpdatedAt();
        this.seatIds = booking.getBookingSeats()
                .stream()
                .map((bs)->{
                    return bs.getSeatId();
                })
                .toList();
        
    }
}
