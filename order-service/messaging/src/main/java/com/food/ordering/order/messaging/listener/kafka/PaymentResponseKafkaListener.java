package com.food.ordering.order.messaging.listener.kafka;

import com.food.ordering.kafka.consumer.service.KafkaConsumer;
import com.food.ordering.kafka.order.model.PaymentStatus;
import com.food.ordering.order.domain.dto.message.PaymentResponse;
import com.food.ordering.order.domain.exception.OrderNotFoundException;
import com.food.ordering.order.domain.port.input.listener.PaymentResponseListener;
import com.food.ordering.order.messaging.mapper.OrderMessagingMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class PaymentResponseKafkaListener implements KafkaConsumer<com.food.ordering.kafka.order.model.PaymentResponse> {

    private final OrderMessagingMapper orderMessagingMapper;
    private final PaymentResponseListener paymentResponseListener;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}", topics = "${order-service.payment-response-topic}")
    public void receive(@Payload List<com.food.ordering.kafka.order.model.PaymentResponse> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} payment responses received with keys: {}, partitions: {}, offsets: {}", messages.size(),
                keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(paymentResponseAvroModel -> {
            try {
                if (PaymentStatus.COMPLETED == paymentResponseAvroModel.getPaymentStatus()) {
                    log.info("Payment completed for order with id: {}", paymentResponseAvroModel.getOrderId());
                    PaymentResponse paymentResponse = orderMessagingMapper.paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel);
                    paymentResponseListener.paymentCompleted(paymentResponse);
                } else if (PaymentStatus.CANCELLED == paymentResponseAvroModel.getPaymentStatus() ||
                        PaymentStatus.FAILED == paymentResponseAvroModel.getPaymentStatus()) {
                    log.info("Payment failed for order with id: {}", paymentResponseAvroModel.getOrderId());
                    PaymentResponse paymentResponse = orderMessagingMapper.paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel);
                    paymentResponseListener.paymentCancelled(paymentResponse);
                }
            } catch (OptimisticLockingFailureException e) {
                // No need to retry a message for OptimisticLockingFailureException. Simply log and move on!
                log.error("Optimistic locking failed in " + this.getClass().getName() + " for order id: {}",
                        paymentResponseAvroModel.getOrderId());
            } catch (OrderNotFoundException e) {
                // No need to retry a message for OrderNotFoundException
                log.error("No order found with order id: {}", paymentResponseAvroModel.getOrderId());
            }
        });
    }
}
