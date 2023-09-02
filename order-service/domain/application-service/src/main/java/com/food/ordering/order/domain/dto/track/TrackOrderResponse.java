package com.food.ordering.order.domain.dto.track;

import com.food.ordering.domain.valueobject.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Getter
public class TrackOrderResponse {

    @NotNull
    private final UUID trackingId;
    @NotNull
    private final OrderStatus orderStatus;
    private final List<String> failureMessages;
}
