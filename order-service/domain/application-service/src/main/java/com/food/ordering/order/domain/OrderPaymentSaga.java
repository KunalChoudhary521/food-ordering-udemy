package com.food.ordering.order.domain;

import com.food.ordering.domain.event.EmptyEvent;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.order.domain.dto.message.PaymentResponse;
import com.food.ordering.order.domain.entity.Order;
import com.food.ordering.order.domain.event.OrderPaidEvent;
import com.food.ordering.order.domain.exception.OrderNotFoundException;
import com.food.ordering.order.domain.port.output.publisher.OrderPaidRestaurantRequestPublisher;
import com.food.ordering.order.domain.port.output.repository.OrderRepository;
import com.food.ordering.saga.SagaStep;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class OrderPaymentSaga implements SagaStep<PaymentResponse, OrderPaidEvent, EmptyEvent> {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final OrderPaidRestaurantRequestPublisher orderPaidRestaurantRequestPublisher;

    @Override
    @Transactional
    public OrderPaidEvent process(PaymentResponse paymentResponse) {
        log.info("Completing payment for order with id: {}", paymentResponse.getOrderId());
        Order order = findOrder(paymentResponse.getOrderId());
        OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order, orderPaidRestaurantRequestPublisher);
        orderRepository.save(order);
        log.info("Order with id: {} is paid", order.getId().getValue());
        return orderPaidEvent;
    }

    @Override
    @Transactional
    public EmptyEvent rollback(PaymentResponse paymentResponse) {
        log.info("Cancelling payment for order with id: {}", paymentResponse.getOrderId());
        Order order = findOrder(paymentResponse.getOrderId());
        orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
        orderRepository.save(order);
        log.info("Order with id: {} is cancelled", order.getId().getValue());
        return EmptyEvent.INSTANCE;
    }

    private Order findOrder(String orderId) {
        return orderRepository.findById(new OrderId(UUID.fromString(orderId)))
                .orElseThrow(() -> {
                    String errorMessage = String.format("Order with id: %s not found", orderId);
                    log.error(errorMessage);
                    return new OrderNotFoundException(errorMessage);
                });
    }
}
