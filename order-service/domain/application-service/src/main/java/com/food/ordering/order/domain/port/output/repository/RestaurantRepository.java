package com.food.ordering.order.domain.port.output.repository;

import com.food.ordering.order.domain.entity.Restaurant;

import java.util.Optional;

public interface RestaurantRepository {

    Optional<Restaurant> findRestaurant(Restaurant restaurant);
}
