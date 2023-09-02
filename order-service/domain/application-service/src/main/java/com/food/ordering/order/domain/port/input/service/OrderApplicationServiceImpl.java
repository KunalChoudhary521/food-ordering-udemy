package com.food.ordering.order.domain.port.input.service;

import com.food.ordering.order.domain.ApplicationDomainEventPublisher;
import com.food.ordering.order.domain.OrderDomainService;
import com.food.ordering.order.domain.dto.create.CreateOrderCommand;
import com.food.ordering.order.domain.dto.create.CreateOrderResponse;
import com.food.ordering.order.domain.dto.track.TrackOrderQuery;
import com.food.ordering.order.domain.dto.track.TrackOrderResponse;
import com.food.ordering.order.domain.entity.Order;
import com.food.ordering.order.domain.entity.Restaurant;
import com.food.ordering.order.domain.event.OrderCreatedEvent;
import com.food.ordering.order.domain.exception.OrderDomainException;
import com.food.ordering.order.domain.exception.OrderNotFoundException;
import com.food.ordering.order.domain.mapper.OrderDataMapper;
import com.food.ordering.order.domain.port.output.repository.CustomerRepository;
import com.food.ordering.order.domain.port.output.repository.OrderRepository;
import com.food.ordering.order.domain.port.output.repository.RestaurantRepository;
import com.food.ordering.order.domain.valueobject.TrackingId;
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
class OrderApplicationServiceImpl implements OrderApplicationService {

    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderDataMapper orderDataMapper;
    private final ApplicationDomainEventPublisher applicationDomainEventPublisher;

    @Transactional
    @Override
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
        checkCustomer(createOrderCommand.getCustomerId());
        Restaurant restaurant = checkRestaurant(createOrderCommand.getRestaurantId());
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitOrder(order, restaurant);
        Order savedOrder = saveOrder(order);
        log.info("Order is saved with id: {}", savedOrder.getId());
        applicationDomainEventPublisher.publish(orderCreatedEvent);
        return orderDataMapper.orderToCreateOrderResponse(savedOrder, "Order created successfully");
    }

    @Transactional(readOnly = true)
    @Override
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        return orderRepository.findByTrackingId(new TrackingId(trackOrderQuery.getTrackingId()))
                .map(orderDataMapper::orderToTrackOrderResponse)
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

    private Restaurant checkRestaurant(UUID restaurantId) {
        return restaurantRepository.findRestaurant(restaurantId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Order not created. Restaurant with id: %s not found", restaurantId);
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
