package com.food.ordering.payment.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "payment-service")
public class PaymentServiceConfig {
    private String paymentRequestTopic;
    private String paymentResponseTopic;
}
