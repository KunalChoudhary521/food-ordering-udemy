package com.food.ordering.order.data.access.order.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "orders_items")
@IdClass(OrderItemEntityId.class)
@Entity
public class OrderItemEntity {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @Id
    @ManyToOne(cascade = CascadeType.ALL) // TODO: Add a test where deleting order also deletes associated items
    @JoinColumn(name = "ORDER_ID")
    @EqualsAndHashCode.Include
    private OrderEntity order;

    private UUID productId;
    private Integer quantity;
    private BigDecimal subTotal;
    private BigDecimal price;
}
