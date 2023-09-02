package com.food.ordering.order.domain.dto.create;

import com.food.ordering.domain.valueobject.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@AllArgsConstructor
@Getter
public class CreateOrderResponse {

    @NotNull
    private final UUID trackingId;
    @NotNull
    private final OrderStatus orderStatus;
    @NotNull
    private final String message;
}
