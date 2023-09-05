package com.food.ordering.order.data.access.order.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "order_address") // TODO: Rename to "order_addresses"
@Entity
public class OrderAddressEntity {

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL)// TODO: Add a test where deleting order also deletes associated address
    @JoinColumn(name = "ORDER_ID")
    private OrderEntity order;

    private String street;
    private String city;
    private String country;
    private String postalCode;
}
