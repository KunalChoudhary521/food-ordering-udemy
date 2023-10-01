package com.food.ordering.order.domain.port.input.listener;

import com.food.ordering.order.domain.OrderApprovalSaga;
import com.food.ordering.order.domain.dto.message.RestaurantApprovalResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@AllArgsConstructor
@Validated
@Slf4j
public class RestaurantApprovalResponseListenerImpl implements RestaurantApprovalResponseListener {

    private final static String FAILURE_MESSAGE_DELIMITER = ",";

    private final OrderApprovalSaga orderApprovalSaga;

    @Override
    public void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse) {
        orderApprovalSaga.process(restaurantApprovalResponse);
        log.info("Order is approved for order id: {}", restaurantApprovalResponse.getOrderId());
    }

    @Override
    public void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse) {
        orderApprovalSaga.rollback(restaurantApprovalResponse);
        log.info("Order Approval Saga rollback operation is completed for order id: {} with failure messages: {}",
                restaurantApprovalResponse.getOrderId(),
                String.join(FAILURE_MESSAGE_DELIMITER, restaurantApprovalResponse.getFailureMessages()));
    }
}
