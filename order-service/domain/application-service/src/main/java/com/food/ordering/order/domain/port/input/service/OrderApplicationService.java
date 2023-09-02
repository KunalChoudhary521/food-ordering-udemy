package com.food.ordering.order.domain.port.input.service;

import com.food.ordering.order.domain.dto.create.CreateOrderCommand;
import com.food.ordering.order.domain.dto.create.CreateOrderResponse;
import com.food.ordering.order.domain.dto.track.TrackOrderQuery;
import com.food.ordering.order.domain.dto.track.TrackOrderResponse;
import jakarta.validation.Valid;

public interface OrderApplicationService {

    CreateOrderResponse createOrder(@Valid CreateOrderCommand createOrderCommand);
    TrackOrderResponse trackOrder(@Valid TrackOrderQuery trackOrderQuery);
}
