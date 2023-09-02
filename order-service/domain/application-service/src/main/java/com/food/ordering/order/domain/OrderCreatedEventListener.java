package com.food.ordering.order.domain;

import com.food.ordering.order.domain.event.OrderCreatedEvent;
import com.food.ordering.order.domain.port.output.publisher.OrderCreatedPaymentRequestPublisher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@AllArgsConstructor
@Component
@Slf4j
public class OrderCreatedEventListener {

    private final OrderCreatedPaymentRequestPublisher orderCreatedPaymentRequestPublisher;

    @TransactionalEventListener
    public void process(OrderCreatedEvent orderCreatedEvent) {
        orderCreatedPaymentRequestPublisher.publish(orderCreatedEvent);
    }
}
