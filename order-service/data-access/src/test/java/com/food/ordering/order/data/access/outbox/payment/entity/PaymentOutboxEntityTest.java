package com.food.ordering.order.data.access.outbox.payment.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PaymentOutboxEntityTest {

    private static final UUID UUID_1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID UUID_2 = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @Test
    void sameIds_equalsAndHashCode_returnTrue() {
        PaymentOutboxEntity entity1 = PaymentOutboxEntity.builder().id(UUID_1).build();
        PaymentOutboxEntity entity2 = PaymentOutboxEntity.builder().id(UUID_1).build();

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    void differentIds_equalsAndHashCode_returnFalse() {
        PaymentOutboxEntity entity1 = PaymentOutboxEntity.builder().id(UUID_1).build();
        PaymentOutboxEntity entity2 = PaymentOutboxEntity.builder().id(UUID_2).build();

        assertNotEquals(entity1, entity2);
        assertNotEquals(entity1.hashCode(), entity2.hashCode());
    }
}