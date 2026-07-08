package com.seatnova.paymentservice.service;

import com.seatnova.paymentservice.dto.CreateOrderRequest;
import com.seatnova.paymentservice.dto.RefundRequest;
import com.seatnova.paymentservice.dto.CreateOrderResponse;
import com.seatnova.paymentservice.dto.PaymentResponse;
import com.seatnova.paymentservice.dto.RefundResponse;

import java.util.UUID;

public interface PaymentService {

    CreateOrderResponse createOrder(CreateOrderRequest request);

    PaymentResponse getPayment(UUID bookingId);

    RefundResponse refundPayment(RefundRequest request);

    void handleWebhook(String payload, String signature);

    void markPaymentSuccess(
            String razorpayOrderId,
            String razorpayPaymentId,
            String method
    );

    void markPaymentFailed(
            String razorpayOrderId,
            String reason
    );
}
