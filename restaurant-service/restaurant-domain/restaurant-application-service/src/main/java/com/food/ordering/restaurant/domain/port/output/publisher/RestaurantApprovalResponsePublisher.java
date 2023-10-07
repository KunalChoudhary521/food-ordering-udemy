package com.food.ordering.restaurant.domain.port.output.publisher;

import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.restaurant.domain.outbox.model.OrderOutboxMessage;

import java.util.function.BiConsumer;

public interface RestaurantApprovalResponsePublisher {
    void publish(OrderOutboxMessage orderOutboxMessage, BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback);
}
