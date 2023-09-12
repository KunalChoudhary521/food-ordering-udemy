package com.food.ordering.payment.domain.port.input.listener;

import com.food.ordering.domain.event.DomainEventPublisher;
import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.payment.domain.PaymentDomainService;
import com.food.ordering.payment.domain.dto.PaymentRequest;
import com.food.ordering.payment.domain.entity.CreditEntry;
import com.food.ordering.payment.domain.entity.CreditHistory;
import com.food.ordering.payment.domain.entity.Payment;
import com.food.ordering.payment.domain.event.PaymentCancelledEvent;
import com.food.ordering.payment.domain.event.PaymentCompletedEvent;
import com.food.ordering.payment.domain.event.PaymentEvent;
import com.food.ordering.payment.domain.event.PaymentFailedEvent;
import com.food.ordering.payment.domain.exception.PaymentApplicationServiceException;
import com.food.ordering.payment.domain.mapper.PaymentMapper;
import com.food.ordering.payment.domain.port.output.repository.CreditEntryRepository;
import com.food.ordering.payment.domain.port.output.repository.CreditHistoryRepository;
import com.food.ordering.payment.domain.port.output.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Validated
@AllArgsConstructor
@Slf4j
public class PaymentRequestListenerImpl implements PaymentRequestListener {

    private final PaymentDomainService paymentDomainService;
    private final PaymentMapper paymentDataMapper;
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final DomainEventPublisher<PaymentCompletedEvent> paymentCompletedDomainEventPublisher;
    private final DomainEventPublisher<PaymentCancelledEvent> paymentCancelledDomainEventPublisher;
    private final DomainEventPublisher<PaymentFailedEvent> paymentFailedDomainEventPublisher;

    @Override
    @Transactional
    public void completePayment(PaymentRequest paymentRequest) {
        log.info("Received payment complete event for order id: {}", paymentRequest.getOrderId());
        Payment payment = paymentDataMapper.paymentRequestModelToPayment(paymentRequest);
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistory(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService.validateAndInitiatePayment(payment, creditEntry, creditHistories,
                failureMessages, paymentCompletedDomainEventPublisher, paymentFailedDomainEventPublisher);
        persistDbObjects(payment, creditEntry, creditHistories, failureMessages);

        publishPaymentEvent(paymentEvent);
    }

    @Override
    @Transactional
    public void cancelPayment(PaymentRequest paymentRequest) {
        log.info("Received payment rollback event for order id: {}", paymentRequest.getOrderId());

        Payment payment = paymentRepository.findByOrderId(UUID.fromString(paymentRequest.getOrderId()))
                .orElseThrow(() -> {
                    String errorMessage = String.format("Payment with order id: %s not found", paymentRequest.getOrderId());
                    log.error(errorMessage);
                    throw new PaymentApplicationServiceException(errorMessage);
                });

        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistories = getCreditHistory(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService.validateAndCancelPayment(payment, creditEntry, creditHistories,
                failureMessages, paymentCancelledDomainEventPublisher, paymentFailedDomainEventPublisher);
        persistDbObjects(payment, creditEntry, creditHistories, failureMessages);

        publishPaymentEvent(paymentEvent);
    }

    private CreditEntry getCreditEntry(CustomerId customerId) {
        return creditEntryRepository.findByCustomerId(customerId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Could not find credit entry for customer: %s",
                            customerId.getValue());
                    log.error(errorMessage);
                    throw new PaymentApplicationServiceException(errorMessage);
                });
    }

    private List<CreditHistory> getCreditHistory(CustomerId customerId) {
        return creditHistoryRepository.findByCustomerId(customerId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Could not find credit history for customer: %s",
                            customerId.getValue());
                    log.error(errorMessage);
                    throw new PaymentApplicationServiceException(errorMessage);
                });
    }

    private void persistDbObjects(Payment payment,
                                  CreditEntry creditEntry,
                                  List<CreditHistory> creditHistories,
                                  List<String> failureMessages) {
        paymentRepository.save(payment);
        if (failureMessages.isEmpty()) {
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
        }
    }

    private void publishPaymentEvent(PaymentEvent paymentEvent) {
        log.info("Publishing payment event with payment id: {} and order id: {}", paymentEvent.getPayment().getId(),
                paymentEvent.getPayment().getOrderId());

        paymentEvent.publish();
    }
}
