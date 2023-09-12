package com.food.ordering.payment.domain.entity;

import com.food.ordering.domain.entity.BaseEntity;
import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.payment.domain.valueobject.CreditHistoryId;
import com.food.ordering.payment.domain.valueobject.TransactionType;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreditHistory extends BaseEntity<CreditHistoryId> {

    private final CustomerId customerId;
    private final Money amount;
    private final TransactionType transactionType;

    @Builder
    public CreditHistory(CreditHistoryId creditHistoryId, CustomerId customerId, Money amount, TransactionType transactionType) {
        super.setId(creditHistoryId);
        this.customerId = customerId;
        this.amount = amount;
        this.transactionType = transactionType;
    }
}
