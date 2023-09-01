package com.food.ordering.order.domain.entity;

import com.food.ordering.domain.entity.BaseEntity;
import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.domain.valueobject.ProductId;
import lombok.Getter;

@Getter
public class Product extends BaseEntity<ProductId> {

    private String name;
    private Money price;

    public Product(ProductId productId, String name, Money price) {
        super.setId(productId);
        this.name = name;
        this.price = price;
    }

    public void update(String name, Money price) {
        this.name = name;
        this.price = price;
    }
}
