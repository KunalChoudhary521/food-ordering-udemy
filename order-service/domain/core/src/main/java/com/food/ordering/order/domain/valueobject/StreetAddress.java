package com.food.ordering.order.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class StreetAddress {

    @EqualsAndHashCode.Exclude
    private final UUID id;
    private final String street;
    private final String city;
    private final String country;
    private final String postalCode;
}
