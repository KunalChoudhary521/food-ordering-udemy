package com.food.ordering.payment.domain.port.output.publisher;

import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.payment.domain.outbox.model.OrderOutboxMessage;

import java.util.function.BiConsumer;

public interface PaymentResponsePublisher {
    void publish(OrderOutboxMessage orderOutboxMessage, BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback);
}
