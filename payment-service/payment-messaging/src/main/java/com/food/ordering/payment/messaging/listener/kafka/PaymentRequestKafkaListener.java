package com.food.ordering.payment.messaging.listener.kafka;

import com.food.ordering.kafka.consumer.service.KafkaConsumer;
import com.food.ordering.kafka.order.model.PaymentOrderStatus;
import com.food.ordering.payment.domain.dto.PaymentRequest;
import com.food.ordering.payment.messaging.mapper.PaymentMessagingMapper;
import com.food.ordering.payment.domain.port.input.listener.PaymentRequestListener;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class PaymentRequestKafkaListener implements KafkaConsumer<com.food.ordering.kafka.order.model.PaymentRequest> {

    private final PaymentRequestListener paymentRequestListener;
    private final PaymentMessagingMapper paymentMessagingMapper;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}", topics = "${payment-service.payment-request-topic}")
    public void receive(@Payload List<com.food.ordering.kafka.order.model.PaymentRequest> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} payment requests received with keys: {}, partitions: {}, offsets: {}", messages.size(),
                keys.toString(), partitions.toString(), offsets.toString());

        messages.forEach(paymentRequestAvroModel -> {
            if (PaymentOrderStatus.PENDING.equals(paymentRequestAvroModel.getPaymentOrderStatus())) {
                log.info("Payment pending for order with id: {}", paymentRequestAvroModel.getOrderId());
                PaymentRequest paymentRequest = paymentMessagingMapper.paymentRequestAvroModelToPaymentRequest(paymentRequestAvroModel);
                paymentRequestListener.completePayment(paymentRequest);
            } else if (PaymentOrderStatus.CANCELLED.equals(paymentRequestAvroModel.getPaymentOrderStatus())) {
                log.info("Payment cancelled for order with id: {}", paymentRequestAvroModel.getOrderId());
                PaymentRequest paymentRequest = paymentMessagingMapper.paymentRequestAvroModelToPaymentRequest(paymentRequestAvroModel);
                paymentRequestListener.cancelPayment(paymentRequest);
            }
        });
    }
}
