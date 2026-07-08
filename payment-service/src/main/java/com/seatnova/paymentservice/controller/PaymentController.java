package com.seatnova.paymentservice.controller;

import com.seatnova.paymentservice.dto.*;
import com.seatnova.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/order")
    public ResponseEntity<CreateOrderResponse> createOrder(
            @RequestBody @Valid CreateOrderRequest request) {

        return ResponseEntity.ok(
                paymentService.createOrder(request));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable UUID bookingId) {

        return ResponseEntity.ok(
                paymentService.getPayment(bookingId));
    }

    @PostMapping("/refund")
    public ResponseEntity<RefundResponse> refund(
            @RequestBody @Valid RefundRequest request) {

        return ResponseEntity.ok(
                paymentService.refundPayment(request));
    }

}
