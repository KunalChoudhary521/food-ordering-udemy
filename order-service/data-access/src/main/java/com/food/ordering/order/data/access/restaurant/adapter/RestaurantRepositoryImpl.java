package com.food.ordering.order.data.access.restaurant.adapter;

import com.food.ordering.order.data.access.restaurant.mapper.RestaurantEntityMapper;
import com.food.ordering.order.data.access.restaurant.respository.RestaurantJpaRepository;
import com.food.ordering.order.domain.entity.Restaurant;
import com.food.ordering.order.domain.port.output.repository.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Component
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final RestaurantEntityMapper restaurantEntityMapper;

    @Override
    public Optional<Restaurant> findRestaurant(Restaurant restaurant) {
        List<UUID> products = restaurantEntityMapper.restaurantToRestaurantProducts(restaurant);
        return restaurantJpaRepository.findByIdAndProductIdIn(restaurant.getId().getValue(), products)
                .map(restaurantEntityMapper::restaurantEntitiesToRestaurant);
    }
}
