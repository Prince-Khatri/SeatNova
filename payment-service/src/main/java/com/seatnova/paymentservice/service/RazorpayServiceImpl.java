package com.seatnova.paymentservice.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;
import com.razorpay.Utils;
import com.seatnova.paymentservice.config.RazorpayProperties;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "payment.gateway.mock-enabled",
        havingValue = "false"
)
public class RazorpayServiceImpl implements RazorpayService {

    private final RazorpayClient razorpayClient;

    private final RazorpayProperties properties;

    @Override
    public Order createOrder(BigDecimal amount) {

        try {

            JSONObject request = new JSONObject();
            request.put("amount", amount
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.HALF_UP)
                    .longValueExact());
            request.put("currency", "INR");
            request.put("receipt", "sn_"
                    + UUID.randomUUID().toString()
                    .replace("-", ""));

            return razorpayClient.orders.create(request);

        } catch (RazorpayException | ArithmeticException ex) {

            throw new PaymentException(
                    "Unable to create Razorpay order",
                    ex
            );
        }
    }

    @Override
    public boolean verifyWebhook(
            String payload,
            String signature) {

        try {

            Utils.verifyWebhookSignature(
                    payload,
                    signature,
                    properties.getWebhookSecret()
            );

            return true;

        } catch (RazorpayException ex) {

            return false;
        }
    }

    @Override
    public Refund refund(String paymentId) {

        try {

            return razorpayClient.payments.refund(paymentId);

        } catch (RazorpayException ex) {

            throw new PaymentException(
                    "Unable to refund Razorpay payment",
                    ex
            );
        }
    }
}
