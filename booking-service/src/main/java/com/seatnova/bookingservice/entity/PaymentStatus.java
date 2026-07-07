package com.seatnova.bookingservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


public enum PaymentStatus {
    SUCESSFUL, PENDING, ABORTED

}