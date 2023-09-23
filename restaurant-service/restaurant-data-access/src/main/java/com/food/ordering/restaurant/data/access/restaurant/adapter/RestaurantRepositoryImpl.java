package com.food.ordering.restaurant.data.access.restaurant.adapter;

import com.food.ordering.common.data.access.restaurant.repository.RestaurantJpaRepository;
import com.food.ordering.payment.domain.port.output.repository.RestaurantRepository;
import com.food.ordering.restaurant.data.access.restaurant.mapper.RestaurantDataMapper;
import com.food.ordering.restaurant.domain.entity.Restaurant;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Component
public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final RestaurantDataMapper restaurantDataMapper;

    @Override
    public Optional<Restaurant> findRestaurant(Restaurant restaurant) {
        List<UUID> products = restaurantDataMapper.restaurantToRestaurantProducts(restaurant);
        return restaurantJpaRepository.findByIdAndProductIdIn(restaurant.getId().getValue(), products)
                .map(restaurantDataMapper::restaurantEntityToRestaurant);
    }
}
