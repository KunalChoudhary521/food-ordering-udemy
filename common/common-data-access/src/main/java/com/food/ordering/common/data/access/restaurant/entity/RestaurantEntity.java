package com.food.ordering.common.data.access.restaurant.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "order_restaurant_m_view", schema = "restaurant")
@IdClass(RestaurantEntityId.class)
@Entity
public class RestaurantEntity {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    @Id
    @EqualsAndHashCode.Include
    private UUID productId;

    private String name;
    private Boolean active;
    private String productName;
    private BigDecimal productPrice;
    private Boolean productAvailable;
}
