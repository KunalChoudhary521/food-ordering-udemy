package com.food.ordering.order.data.access.order.adapter;

import com.food.ordering.domain.valueobject.OrderId;
import com.food.ordering.order.data.access.order.entity.OrderEntity;
import com.food.ordering.order.data.access.order.mapper.OrderEntityMapper;
import com.food.ordering.order.data.access.order.repository.OrderJpaRepository;
import com.food.ordering.order.domain.entity.Order;
import com.food.ordering.order.domain.port.output.repository.OrderRepository;
import com.food.ordering.order.domain.valueobject.TrackingId;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@Component
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderEntityMapper orderEntityMapper;

    @Override
    public Order save(Order order) {
        OrderEntity orderEntity = orderEntityMapper.orderToOrderEntity(order);
        // TODO: Find alternative to Attaching orderEntity to OrderItemEntities for persistence
        orderEntity.getItems().forEach(item -> item.setOrder(orderEntity));
        OrderEntity savedOrderEntity = orderJpaRepository.save(orderEntity);
        return orderEntityMapper.orderEntityToOrder(savedOrderEntity);
    }

    @Override
    public Optional<Order> findByTrackingId(TrackingId trackingId) {
        return orderJpaRepository.findByTrackingId(trackingId.getValue())
                .map(orderEntityMapper::orderEntityToOrder);
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return orderJpaRepository.findById(orderId.getValue())
                .map(orderEntityMapper::orderEntityToOrder);
    }
}
