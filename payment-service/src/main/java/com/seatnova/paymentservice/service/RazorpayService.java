package com.seatnova.paymentservice.service;

import com.razorpay.Order;
import com.razorpay.Refund;

import java.math.BigDecimal;

public interface RazorpayService {

    Order createOrder(BigDecimal amount) throws PaymentException;

    boolean verifyWebhook(String payload,
                          String signature);

    Refund refund(String paymentId) throws PaymentException;

}
