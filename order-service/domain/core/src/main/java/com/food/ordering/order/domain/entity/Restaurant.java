package com.food.ordering.order.domain.entity;

import com.food.ordering.domain.entity.AggregateRoot;
import com.food.ordering.domain.valueobject.RestaurantId;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class Restaurant extends AggregateRoot<RestaurantId> {

    private final List<Product> products;
    private boolean active;

    @Builder
    public Restaurant(RestaurantId id, List<Product> products, boolean active) {
        super.setId(id);
        this.products = products;
        this.active = active;
    }
}
