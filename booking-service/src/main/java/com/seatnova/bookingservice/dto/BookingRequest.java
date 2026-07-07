package com.seatnova.bookingservice.dto;

import com.seatnova.bookingservice.entity.BookingSeat;
import com.seatnova.bookingservice.entity.BookingStatus;
import com.seatnova.bookingservice.entity.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class BookingRequest {
    @NotNull
    private UUID userId;
    @NotNull
    private UUID showId;
    @NotNull
    private BigDecimal totalAmount;
    @NotEmpty
    private List<UUID> seatIds;

}
