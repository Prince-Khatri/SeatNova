package com.seatnova.paymentservice.config;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RazorpayConfig {

    private final RazorpayProperties properties;

    @Bean
    @ConditionalOnProperty(
            name = "payment.gateway.mock-enabled",
            havingValue = "false"
    )
    public RazorpayClient razorpayClient()
            throws RazorpayException {

        return new RazorpayClient(
                properties.getKeyId(),
                properties.getKeySecret());
    }

}
