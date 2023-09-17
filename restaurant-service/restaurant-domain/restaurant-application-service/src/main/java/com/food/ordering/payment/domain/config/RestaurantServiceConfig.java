package com.food.ordering.payment.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "restaurant-service")
public class RestaurantServiceConfig {
    private String restaurantApprovalRequestTopic;
    private String restaurantApprovalResponseTopic;
}
