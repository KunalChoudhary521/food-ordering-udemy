package com.food.ordering.order.domain.port.output.repository;

import com.food.ordering.order.domain.entity.Restaurant;

import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepository {

    Optional<Restaurant> findRestaurant(UUID restaurantId);
}
