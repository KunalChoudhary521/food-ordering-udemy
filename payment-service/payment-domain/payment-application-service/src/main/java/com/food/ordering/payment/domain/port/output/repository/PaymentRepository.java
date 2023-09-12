package com.food.ordering.payment.domain.port.output.repository;

import com.food.ordering.payment.domain.entity.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Payment save(Payment payment);
    Optional<Payment> findByOrderId(UUID orderId);
}
