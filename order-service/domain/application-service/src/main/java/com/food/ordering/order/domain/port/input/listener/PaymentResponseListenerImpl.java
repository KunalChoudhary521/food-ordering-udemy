package com.food.ordering.order.domain.port.input.listener;

import com.food.ordering.order.domain.OrderPaymentSaga;
import com.food.ordering.order.domain.dto.message.PaymentResponse;
import com.food.ordering.order.domain.event.OrderPaidEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@AllArgsConstructor
@Validated
@Slf4j
public class PaymentResponseListenerImpl implements PaymentResponseListener {

    private final static String FAILURE_MESSAGE_DELIMITER = ",";

    private final OrderPaymentSaga orderPaymentSaga;

    @Override
    public void paymentCompleted(PaymentResponse paymentResponse) {
        OrderPaidEvent orderPaidEvent = orderPaymentSaga.process(paymentResponse);
        log.info("Publishing OrderPaidEvent for order id: {}", paymentResponse.getOrderId());
        orderPaidEvent.publish();
    }

    @Override
    public void paymentCancelled(PaymentResponse paymentResponse) {
        orderPaymentSaga.rollback(paymentResponse);
        log.info("Order is rolled back for order id: {} with failure messages: {}", paymentResponse.getOrderId(),
                String.join(FAILURE_MESSAGE_DELIMITER, paymentResponse.getFailureMessages()));
    }
}
