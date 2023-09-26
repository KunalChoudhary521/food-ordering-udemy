package com.food.ordering.order.domain;

import com.food.ordering.domain.event.EmptyEvent;
import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.order.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.order.domain.entity.Order;
import com.food.ordering.order.domain.event.OrderCancelledEvent;
import com.food.ordering.order.domain.exception.OrderNotFoundException;
import com.food.ordering.order.domain.port.output.publisher.OrderCancelledPaymentRequestPublisher;
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
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse, EmptyEvent, OrderCancelledEvent> {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final OrderCancelledPaymentRequestPublisher orderCancelledPaymentRequestPublisher;

    @Override
    @Transactional
    public EmptyEvent process(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Approving order with id: {}", restaurantApprovalResponse.getOrderId());
        Order order = findOrder(restaurantApprovalResponse.getOrderId());
        orderDomainService.approveOrder(order);
        orderRepository.save(order);
        log.info("Order with id: {} is approved", order.getId().getValue());
        return EmptyEvent.INSTANCE;
    }

    @Override
    @Transactional
    public OrderCancelledEvent rollback(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Cancelling order with id: {}", restaurantApprovalResponse.getOrderId());
        Order order = findOrder(restaurantApprovalResponse.getOrderId());
        OrderCancelledEvent orderCancelledEvent = orderDomainService.cancelOrderPayment(order, restaurantApprovalResponse.getFailureMessages(),
                orderCancelledPaymentRequestPublisher);
        orderRepository.save(order);
        log.info("Order with id: {} is cancelling", order.getId().getValue());
        return orderCancelledEvent;
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
