package com.food.ordering.kafka.producer.service;

import com.food.ordering.kafka.order.model.PaymentOrderStatus;
import com.food.ordering.kafka.order.model.PaymentRequest;
import com.food.ordering.order.container.OrderServiceApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = OrderServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = "${order-service.payment-request-topic}", partitions = 1,
        bootstrapServersProperty = "kafka-config.bootstrap-servers")
@ActiveProfiles("test")
class KafkaProducerTest {

    @Value("${order-service.payment-request-topic}")
    private String paymentRequestTopic;

    @Autowired
    private KafkaProducer<String, PaymentRequest> kafkaProducer;

    @Test
    void paymentRequestAvroModel_send_messagePublishedByKafka() {
        PaymentRequest paymentRequest = PaymentRequest.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(UUID.randomUUID().toString())
                .setOrderId(UUID.randomUUID().toString())
                .setCustomerId(UUID.randomUUID().toString())
                .setPrice(new BigDecimal("20.23"))
                .setCreatedAt(ZonedDateTime.now().toInstant())
                .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
                .build();

        String messageKey = UUID.randomUUID().toString();

        BiConsumer<SendResult<String, PaymentRequest>, Throwable> callback = (result, ex) -> {
            assertNotNull(result);
            assertEquals(messageKey, result.getProducerRecord().key());
            assertEquals(paymentRequestTopic, result.getProducerRecord().topic());
            assertEquals(paymentRequest, result.getProducerRecord().value());
        };

        kafkaProducer.send(paymentRequestTopic, messageKey, paymentRequest, callback);
    }
}