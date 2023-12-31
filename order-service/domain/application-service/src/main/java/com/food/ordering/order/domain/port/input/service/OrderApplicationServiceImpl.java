package com.food.ordering.order.domain.port.input.service;

import com.food.ordering.order.domain.OrderDomainService;
import com.food.ordering.order.domain.OrderSagaHelper;
import com.food.ordering.order.domain.dto.create.CreateOrderCommand;
import com.food.ordering.order.domain.dto.create.CreateOrderResponse;
import com.food.ordering.order.domain.dto.track.TrackOrderQuery;
import com.food.ordering.order.domain.dto.track.TrackOrderResponse;
import com.food.ordering.order.domain.entity.Order;
import com.food.ordering.order.domain.entity.Restaurant;
import com.food.ordering.order.domain.event.OrderCreatedEvent;
import com.food.ordering.order.domain.exception.OrderDomainException;
import com.food.ordering.order.domain.exception.OrderNotFoundException;
import com.food.ordering.order.domain.mapper.OrderMapper;
import com.food.ordering.order.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.food.ordering.order.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.food.ordering.order.domain.port.output.repository.CustomerRepository;
import com.food.ordering.order.domain.port.output.repository.OrderRepository;
import com.food.ordering.order.domain.port.output.repository.RestaurantRepository;
import com.food.ordering.order.domain.valueobject.TrackingId;
import com.food.ordering.outbox.OutboxStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@Validated
@AllArgsConstructor
public class OrderApplicationServiceImpl implements OrderApplicationService {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderMapper orderMapper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final OrderSagaHelper orderSagaHelper;

    @Transactional
    @Override
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        checkCustomer(createOrderCommand.getCustomerId());
        Restaurant restaurant = checkRestaurant(createOrderCommand);
        Order order = orderMapper.createOrderCommandToOrder(createOrderCommand);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitOrder(order, restaurant);
        Order savedOrder = saveOrder(order);
        log.info("Order is saved with id: {}", savedOrder.getId());

        CreateOrderResponse createOrderResponse = orderMapper.orderToCreateOrderResponse(savedOrder, "Order created successfully");

        OrderPaymentEventPayload orderPaymentEventPayload = orderMapper.orderCreatedEventToOrderPaymentEventPayload(orderCreatedEvent);
        paymentOutboxHelper.savePaymentOutboxMessage(orderPaymentEventPayload,
                orderCreatedEvent.getOrder().getOrderStatus(),
                orderSagaHelper.orderStatusToSagaStatus(orderCreatedEvent.getOrder().getOrderStatus()),
                OutboxStatus.STARTED, UUID.randomUUID());

        return createOrderResponse;
    }

    @Transactional(readOnly = true)
    @Override
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        return orderRepository.findByTrackingId(new TrackingId(trackOrderQuery.getTrackingId()))
                .map(orderMapper::orderToTrackOrderResponse)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Order not found with tracking id: %s", trackOrderQuery.getTrackingId());
                    log.warn(errorMessage);
                    throw new OrderNotFoundException(errorMessage);
                });
    }

    private void checkCustomer(UUID customerId) {
        customerRepository.findCustomer(customerId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Order not created. Customer with id: %s not found", customerId);
                    log.warn(errorMessage);
                    throw new OrderDomainException(errorMessage);
                });
    }

    private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
        Restaurant restaurant = orderMapper.createOrderCommandToRestaurant(createOrderCommand);
        return restaurantRepository.findRestaurant(restaurant)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Order not created. Restaurant with id: %s not found", restaurant.getId());
                    log.warn(errorMessage);
                    throw new OrderDomainException(errorMessage);
                });
    }

    private Order saveOrder(Order order) {
        return Optional.ofNullable(orderRepository.save(order))
                .orElseThrow(() -> {
                    String errorMessage = String.format("Unable to save order with customer id: %s and restaurant id: %s",
                            order.getCustomerId(), order.getRestaurantId());
                    log.warn(errorMessage);
                    throw new OrderDomainException(errorMessage);
        });
    }
}
