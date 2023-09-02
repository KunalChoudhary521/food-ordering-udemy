package com.food.ordering.order.domain.dto.message;

import com.food.ordering.domain.valueobject.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class PaymentResponse {

    @NotNull
    private final String id;
    private final String sagaId;
    private final String orderId;
    private final String paymentId;
    private final String customerId;
    private final BigDecimal price;
    private final Instant createdAt;
    private final PaymentStatus paymentStatus;
    private final List<String> failureMessages;
}
