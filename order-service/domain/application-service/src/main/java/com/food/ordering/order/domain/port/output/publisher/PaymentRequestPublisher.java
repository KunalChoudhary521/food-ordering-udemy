package com.food.ordering.order.domain.port.output.publisher;

import com.food.ordering.order.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.outbox.OutboxStatus;

import java.util.function.BiConsumer;

public interface PaymentRequestPublisher {

    void publish(OrderPaymentOutboxMessage orderPaymentOutboxMessage,
                 BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxCallback);
}
