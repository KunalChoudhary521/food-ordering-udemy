package com.food.ordering.order.domain.port.output.publisher;

import com.food.ordering.order.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.outbox.OutboxStatus;

import java.util.function.BiConsumer;

public interface RestaurantApprovalRequestPublisher {

    void publish(OrderApprovalOutboxMessage orderApprovalOutboxMessage,
                 BiConsumer<OrderApprovalOutboxMessage, OutboxStatus> outboxCallback);
}
