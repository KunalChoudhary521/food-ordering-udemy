package com.food.ordering.payment.data.access.payment.adapter;

import com.food.ordering.payment.data.access.payment.entity.PaymentEntity;
import com.food.ordering.payment.data.access.payment.mapper.PaymentDataMapper;
import com.food.ordering.payment.data.access.payment.repository.PaymentJpaRepository;
import com.food.ordering.payment.domain.entity.Payment;
import com.food.ordering.payment.domain.port.output.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentDataMapper paymentDataMapper;

    @Override
    public Payment save(Payment payment) {
        PaymentEntity paymentEntity = paymentDataMapper.paymentToPaymentEntity(payment);
        PaymentEntity savedPaymentEntity = paymentJpaRepository.save(paymentEntity);
        return paymentDataMapper.paymentEntityToPayment(savedPaymentEntity);
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return paymentJpaRepository.findByOrderId(orderId).map(paymentDataMapper::paymentEntityToPayment);
    }
}
