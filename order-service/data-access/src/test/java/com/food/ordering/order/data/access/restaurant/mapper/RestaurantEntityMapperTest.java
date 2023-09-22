package com.food.ordering.order.data.access.restaurant.mapper;

import com.food.ordering.common.data.access.restaurant.entity.RestaurantEntity;
import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.domain.valueobject.ProductId;
import com.food.ordering.domain.valueobject.RestaurantId;
import com.food.ordering.order.domain.entity.Product;
import com.food.ordering.order.domain.entity.Restaurant;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RestaurantEntityMapperTest {

    private static final RestaurantId TEST_RESTAURANT_ID_1 = new RestaurantId(UUID.fromString("b9b59acd-e769-418d-8ce7-29a9b012fcdd"));
    private static final String TEST_RESTAURANT_NAME = "Test restaurant name";
    private static final ProductId TEST_PRODUCT_ID_1 = new ProductId(UUID.fromString("1494bff7-592d-4282-9038-854104a62c03"));
    private static final ProductId TEST_PRODUCT_ID_2 = new ProductId(UUID.fromString("2d5138c7-8e81-471e-8b20-da635f922bdc"));
    private static final ProductId TEST_PRODUCT_ID_3 = new ProductId(UUID.fromString("6837370f-2b15-460f-988a-424c0072af4d"));

    private final RestaurantEntityMapper restaurantEntityMapper = new RestaurantEntityMapper();

    @Test
    void restaurantEntities_restaurantEntitiesToRestaurant_restaurant() {
        List<RestaurantEntity> restaurantEntity = List.of(
                RestaurantEntity.builder()
                        .id(TEST_RESTAURANT_ID_1.getValue())
                        .active(true)
                        .productId(TEST_PRODUCT_ID_1.getValue())
                        .name(TEST_RESTAURANT_NAME)
                        .productName("Test p1")
                        .productPrice(new BigDecimal("2.99"))
                        .build(),
                RestaurantEntity.builder()
                        .id(TEST_RESTAURANT_ID_1.getValue())
                        .active(true)
                        .productId(TEST_PRODUCT_ID_2.getValue())
                        .name(TEST_RESTAURANT_NAME)
                        .productName("Test p2")
                        .productPrice(new BigDecimal("5.99"))
                        .build());

        Restaurant restaurant = restaurantEntityMapper.restaurantEntitiesToRestaurant(restaurantEntity);

        assertEquals(restaurantEntity.get(0).getId(), restaurant.getId().getValue());
        assertEquals(restaurantEntity.get(0).getActive(), restaurant.isActive());
        assertEquals(restaurantEntity.get(0).getName(), restaurant.getName());

        assertEquals(2, restaurant.getProducts().size());
        assertEquals(restaurantEntity.get(0).getProductId(), restaurant.getProducts().get(0).getId().getValue());
        assertEquals(restaurantEntity.get(0).getProductName(), restaurant.getProducts().get(0).getName());
        assertEquals(restaurantEntity.get(0).getProductPrice(), restaurant.getProducts().get(0).getPrice().getAmount());

        assertEquals(restaurantEntity.get(1).getProductId(), restaurant.getProducts().get(1).getId().getValue());
        assertEquals(restaurantEntity.get(1).getProductName(), restaurant.getProducts().get(1).getName());
        assertEquals(restaurantEntity.get(1).getProductPrice(), restaurant.getProducts().get(1).getPrice().getAmount());
    }

    @Test
    void restaurant_restaurantToRestaurantProducts_productIds() {
        List<Product> products = List.of(
                new Product(TEST_PRODUCT_ID_1, "p1", new Money(new BigDecimal("4.49"))),
                new Product(TEST_PRODUCT_ID_2, "p2", new Money(new BigDecimal("8.99"))),
                new Product(TEST_PRODUCT_ID_3, "p3", new Money(new BigDecimal("8.99"))));
        Restaurant restaurant = Restaurant.builder()
                .id(TEST_RESTAURANT_ID_1)
                .products(products)
                .build();

        List<UUID> productIds = restaurantEntityMapper.restaurantToRestaurantProducts(restaurant);

        assertEquals(3, productIds.size());
        assertThat(productIds).contains(TEST_PRODUCT_ID_1.getValue(), TEST_PRODUCT_ID_3.getValue(), TEST_PRODUCT_ID_2.getValue());
    }
}