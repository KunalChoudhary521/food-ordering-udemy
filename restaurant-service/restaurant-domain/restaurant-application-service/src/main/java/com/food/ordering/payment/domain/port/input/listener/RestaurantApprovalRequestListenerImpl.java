package com.food.ordering.payment.domain.port.input.listener;

import com.food.ordering.payment.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.payment.domain.mapper.RestaurantMapper;
import com.food.ordering.payment.domain.port.output.publisher.OrderApprovedPublisher;
import com.food.ordering.payment.domain.port.output.publisher.OrderRejectedPublisher;
import com.food.ordering.payment.domain.port.output.repository.OrderApprovalRepository;
import com.food.ordering.payment.domain.port.output.repository.RestaurantRepository;
import com.food.ordering.restaurant.domain.RestaurantDomainService;
import com.food.ordering.restaurant.domain.entity.Restaurant;
import com.food.ordering.restaurant.domain.event.OrderApprovalEvent;
import com.food.ordering.restaurant.domain.exception.RestaurantDomainException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class RestaurantApprovalRequestListenerImpl implements RestaurantApprovalRequestListener {

    private final RestaurantDomainService restaurantDomainService;
    private final RestaurantMapper restaurantMapper;
    private final RestaurantRepository restaurantRepository;
    private final OrderApprovalRepository orderApprovalRepository;
    private final OrderApprovedPublisher orderApprovedPublisher;
    private final OrderRejectedPublisher orderRejectedPublisher;

    @Override
    @Transactional
    public void approveOrder(RestaurantApprovalRequest restaurantApprovalRequest) {
        log.info("Received restaurant approval request for order id: {}", restaurantApprovalRequest.getOrderId());
        List<String> failureMessages = new ArrayList<>();

        Restaurant restaurant = findRestaurant(restaurantApprovalRequest);
        OrderApprovalEvent orderApprovalEvent = restaurantDomainService.validateOrder(restaurant, failureMessages,
                orderApprovedPublisher, orderRejectedPublisher);
        orderApprovalRepository.save(restaurant.getOrderApproval());

        publishOrderApprovalEvent(orderApprovalEvent);
    }


    private Restaurant findRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
        Restaurant restaurant = restaurantMapper.restaurantApprovalRequestToRestaurant(restaurantApprovalRequest);
        return restaurantRepository.findRestaurant(restaurant)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Restaurant with id: %s not found", restaurant.getId().getValue().toString());
                    log.error(errorMessage);
                    throw new RestaurantDomainException(errorMessage);
                });
    }

    private void publishOrderApprovalEvent(OrderApprovalEvent orderApprovalEvent) {
        log.info("Publishing order approval event with restaurant id: {} and order id: {}",
                orderApprovalEvent.getRestaurantId().getValue().toString(),
                orderApprovalEvent.getOrderApproval().getOrderId().getValue().toString());

        orderApprovalEvent.publish();
    }
}
