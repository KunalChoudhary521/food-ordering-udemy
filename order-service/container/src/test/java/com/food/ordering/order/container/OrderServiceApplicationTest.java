package com.food.ordering.order.container;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.domain.valueobject.OrderStatus;
import com.food.ordering.domain.valueobject.ProductId;
import com.food.ordering.order.domain.dto.create.CreateOrderCommand;
import com.food.ordering.order.domain.dto.create.CreateOrderResponse;
import com.food.ordering.order.domain.dto.create.OrderAddress;
import com.food.ordering.order.domain.dto.create.OrderItemDto;
import com.food.ordering.order.domain.dto.track.TrackOrderResponse;
import com.food.ordering.order.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.order.domain.port.output.repository.PaymentOutboxRepository;
import com.food.ordering.outbox.OutboxStatus;
import com.food.ordering.saga.SagaStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.food.ordering.saga.order.SagaConstants.ORDER_SAGA_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = OrderServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderServiceApplicationTest {

    private static final UUID TEST_CUSTOMER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID TEST_RESTAURANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID TEST_TRACKING_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");
    private static final Money TEST_PRICE = new Money(new BigDecimal("25.00"));
    private static final ProductId TEST_PRODUCT_ID_1 = new ProductId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
    private static final ProductId TEST_PRODUCT_ID_2 = new ProductId(UUID.fromString("00000000-0000-0000-0000-000000000002"));

    @Autowired
    private PaymentOutboxRepository paymentOutboxRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void createOrderCommand_createOrder_createOrderResponse() throws Exception {
        List<OrderItemDto> orderItems = createOrderItems();
        BigDecimal totalOrderPrice = orderItems.stream().map(OrderItemDto::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        CreateOrderCommand createOrderCommand = CreateOrderCommand.builder()
                .customerId(TEST_CUSTOMER_ID)
                .restaurantId(TEST_RESTAURANT_ID)
                .price(TEST_PRICE.getAmount())
                .orderItems(orderItems)
                .orderAddress(createOrderAddress())
                .build();

        MvcResult mvcResult = mvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderCommand)))
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(mvcResult);

        CreateOrderResponse createOrderResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CreateOrderResponse.class);
        assertNotNull(createOrderResponse.getTrackingId());
        assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
        assertNotNull(createOrderResponse.getMessage());

        List<OrderPaymentOutboxMessage> orderPaymentOutboxMessages =
                paymentOutboxRepository.findByTypeAndOutboxStatusAndSagaStatus(ORDER_SAGA_NAME, OutboxStatus.STARTED, SagaStatus.STARTED)
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(message -> message.getPayload().contains(TEST_CUSTOMER_ID.toString()) &&
                                        message.getPayload().contains(totalOrderPrice.toString()))
                        .toList();

        assertEquals(1, orderPaymentOutboxMessages.size());
        assertTrue(orderPaymentOutboxMessages.get(0).getPayload().contains(OrderStatus.PENDING.toString()));
    }

    @Test
    void trackingId_trackOrder_trackOrderResponse() throws Exception {

        MvcResult mvcResult = mvc.perform(get("/orders/" + TEST_TRACKING_ID))
                .andExpect(status().isOk())
                .andReturn();

        assertNotNull(mvcResult);

        TrackOrderResponse trackOrderResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TrackOrderResponse.class);
        assertEquals(TEST_TRACKING_ID, trackOrderResponse.getTrackingId());
        assertEquals(OrderStatus.CANCELLED, trackOrderResponse.getOrderStatus());
        assertThat(trackOrderResponse.getFailureMessages()).contains("fail2", "fail1", "fail3");
    }

    private List<OrderItemDto> createOrderItems() {
        return List.of(
                OrderItemDto.builder()
                        .price(new BigDecimal("13.00"))
                        .productId(TEST_PRODUCT_ID_1.getValue())
                        .quantity(1)
                        .subTotal(new BigDecimal("13.00"))
                        .build(),
                OrderItemDto.builder()
                        .price(new BigDecimal("12.00"))
                        .productId(TEST_PRODUCT_ID_2.getValue())
                        .quantity(1)
                        .subTotal(new BigDecimal("12.00"))
                        .build());
    }

    private OrderAddress createOrderAddress() {
        return OrderAddress.builder()
                .street("123 Test Street Rd.")
                .city("Test city")
                .country("Test country")
                .postalCode("12345")
                .build();
    }
}