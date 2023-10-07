package com.food.ordering.restaurant.data.access.restaurant.adapter;

import com.food.ordering.restaurant.data.access.restaurant.entity.OrderApprovalEntity;
import com.food.ordering.restaurant.data.access.restaurant.mapper.RestaurantDataMapper;
import com.food.ordering.restaurant.data.access.restaurant.repository.OrderApprovalJpaRepository;
import com.food.ordering.restaurant.domain.entity.OrderApproval;
import com.food.ordering.restaurant.domain.port.output.repository.OrderApprovalRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class OrderApprovalRepositoryImpl implements OrderApprovalRepository {

    private final OrderApprovalJpaRepository orderApprovalJpaRepository;
    private final RestaurantDataMapper restaurantDataMapper;

    @Override
    public OrderApproval save(OrderApproval orderApproval) {
        OrderApprovalEntity orderApprovalEntity = restaurantDataMapper.orderApprovalToOrderApprovalEntity(orderApproval);
        OrderApprovalEntity savedOrderApprovalEntity = orderApprovalJpaRepository.save(orderApprovalEntity);
        return restaurantDataMapper.orderApprovalEntityToOrderApproval(savedOrderApprovalEntity);
    }
}
