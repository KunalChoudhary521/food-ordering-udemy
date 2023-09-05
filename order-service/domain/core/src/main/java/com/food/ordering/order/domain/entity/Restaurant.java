package com.food.ordering.order.domain.entity;

import com.food.ordering.domain.entity.AggregateRoot;
import com.food.ordering.domain.valueobject.RestaurantId;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class Restaurant extends AggregateRoot<RestaurantId> {

    private final List<Product> products;
    private final String name;
    private boolean active;

    @Builder
    public Restaurant(RestaurantId id, List<Product> products, String name, boolean active) {
        super.setId(id);
        this.products = products;
        this.name = name;
        this.active = active;
    }
}
