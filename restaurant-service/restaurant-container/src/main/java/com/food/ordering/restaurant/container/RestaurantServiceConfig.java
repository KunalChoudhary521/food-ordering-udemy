package com.food.ordering.restaurant.container;

import com.food.ordering.restaurant.domain.RestaurantDomainService;
import com.food.ordering.restaurant.domain.RestaurantDomainServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestaurantServiceConfig {

    @Bean
    public RestaurantDomainService restaurantDomainService() {
        return new RestaurantDomainServiceImpl();
    }
}
