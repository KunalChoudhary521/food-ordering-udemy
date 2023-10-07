package com.food.ordering.restaurant.domain.port.output.repository;

import com.food.ordering.restaurant.domain.entity.Restaurant;

import java.util.Optional;

public interface RestaurantRepository {

    Optional<Restaurant> findRestaurant(Restaurant restaurant);
}
