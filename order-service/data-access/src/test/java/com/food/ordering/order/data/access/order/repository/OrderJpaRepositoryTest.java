package com.food.ordering.order.data.access.order.repository;

import com.food.ordering.domain.valueobject.OrderStatus;
import com.food.ordering.order.data.access.order.entity.OrderEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ContextConfiguration(classes = {OrderJpaRepository.class})
@EnableJpaRepositories(basePackages = "com.food.ordering.order.data.access")
@EntityScan(basePackages = "com.food.ordering.order.data.access")
@ActiveProfiles("test")
@DataJpaTest
class OrderJpaRepositoryTest {

    private static final UUID UUID_1 = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Test
    void trackingIdForUnknownEntity_findByTrackingId_returnOrderEntity() {
        UUID trackingId = UUID.fromString("48fead0d-8629-4e60-ab1e-16e32d8e7c9e");
        Optional<OrderEntity> orderEntity = orderJpaRepository.findByTrackingId(trackingId);

        assertFalse(orderEntity.isPresent());
    }

    @Test
    void trackingIdForExistingEntity_findByTrackingId_returnOrderEntity() {
        Optional<OrderEntity> orderEntityOpt = orderJpaRepository.findByTrackingId(UUID_1);

        assertTrue(orderEntityOpt.isPresent());
        OrderEntity orderEntity = orderEntityOpt.get();

        assertEquals(UUID_1, orderEntity.getId());
        assertEquals(UUID_1, orderEntity.getCustomerId());
        assertEquals(UUID_1, orderEntity.getRestaurantId());
        assertEquals(UUID_1, orderEntity.getTrackingId());
        assertEquals(new BigDecimal("11.49"), orderEntity.getPrice());
        assertEquals(OrderStatus.PAID, orderEntity.getOrderStatus());
        assertThat(orderEntity.getFailureMessages()).contains("fail1", "fail2");

        assertNotNull(orderEntity.getAddress());
        assertEquals(UUID_1, orderEntity.getAddress().getId());
        assertEquals(orderEntity, orderEntity.getAddress().getOrder());
        assertEquals("123 Test St.", orderEntity.getAddress().getStreet());
        assertEquals("Test city", orderEntity.getAddress().getCity());
        assertEquals("Test country", orderEntity.getAddress().getCountry());
        assertEquals("123456", orderEntity.getAddress().getPostalCode());

        assertEquals(2, orderEntity.getItems().size());

        assertEquals(1L, orderEntity.getItems().get(0).getId());
        assertEquals(orderEntity, orderEntity.getItems().get(0).getOrder());
        assertEquals(UUID_1, orderEntity.getItems().get(0).getProductId());
        assertEquals(1, orderEntity.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("2.25"), orderEntity.getItems().get(0).getSubTotal());
        assertEquals(new BigDecimal("2.25"), orderEntity.getItems().get(0).getPrice());

        assertEquals(2L, orderEntity.getItems().get(1).getId());
        assertEquals(orderEntity, orderEntity.getItems().get(1).getOrder());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), orderEntity.getItems().get(1).getProductId());
        assertEquals(2, orderEntity.getItems().get(1).getQuantity());
        assertEquals(new BigDecimal("9.24"), orderEntity.getItems().get(1).getSubTotal());
        assertEquals(new BigDecimal("9.24"), orderEntity.getItems().get(1).getPrice());
    }

    @Test
    void idForExistingEntity_delete_cascadesDeleteToRelatedEntities() {
        orderJpaRepository.deleteById(UUID_1);

        Optional<OrderEntity> orderEntityOpt = orderJpaRepository.findById(UUID_1);

        assertTrue(orderEntityOpt.isEmpty());
    }
}