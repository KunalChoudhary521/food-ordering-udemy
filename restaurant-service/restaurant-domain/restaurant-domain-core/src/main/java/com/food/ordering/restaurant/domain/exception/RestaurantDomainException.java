package com.food.ordering.restaurant.domain.exception;

public class RestaurantDomainException extends RuntimeException {

    public RestaurantDomainException(String message) {
        super(message);
    }

    public RestaurantDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
