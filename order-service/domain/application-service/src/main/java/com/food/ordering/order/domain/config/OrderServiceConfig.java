package com.food.ordering.order.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "order-service")
public class OrderServiceConfig {
    private String paymentRequestTopic;
    private String paymentResponseTopic;
    private String restaurantApprovalRequestTopic;
    private String restaurantApprovalResponseTopic;
}
