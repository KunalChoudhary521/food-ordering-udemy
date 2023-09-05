package com.food.ordering.order.data.access.restaurant.mapper;

import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.domain.valueobject.ProductId;
import com.food.ordering.domain.valueobject.RestaurantId;
import com.food.ordering.order.data.access.restaurant.entity.RestaurantEntity;
import com.food.ordering.order.domain.entity.Product;
import com.food.ordering.order.domain.entity.Restaurant;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

@Component
public class RestaurantEntityMapper {

    public Restaurant restaurantEntitiesToRestaurant(List<RestaurantEntity> restaurantEntities) {
        // TODO: Replace with MapStruct auto-mapping
        Restaurant restaurant = Restaurant.builder().build();
        if(!CollectionUtils.isEmpty(restaurantEntities)) {
            RestaurantEntity firstRestaurantEntity = restaurantEntities.get(0);

            List<Product> products = restaurantEntities.stream()
                    .map(entity -> new Product(new ProductId(entity.getProductId()),
                            entity.getProductName(),
                            new Money(entity.getProductPrice())))
                    .toList();

            restaurant = Restaurant.builder()
                    .id(new RestaurantId(firstRestaurantEntity.getId()))
                    .active(firstRestaurantEntity.getActive())
                    .name(firstRestaurantEntity.getName())
                    .products(products)
                    .build();
        }

        return restaurant;
    }

    public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
        return restaurant.getProducts().stream().map(product -> product.getId().getValue()).toList();
    }
}
