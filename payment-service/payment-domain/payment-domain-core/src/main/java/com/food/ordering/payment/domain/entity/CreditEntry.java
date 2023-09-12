package com.food.ordering.payment.domain.entity;

import com.food.ordering.domain.entity.BaseEntity;
import com.food.ordering.domain.valueobject.CustomerId;
import com.food.ordering.domain.valueobject.Money;
import com.food.ordering.payment.domain.valueobject.CreditEntryId;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CreditEntry extends BaseEntity<CreditEntryId> {

    private final CustomerId customerId;
    private Money totalCreditAmount;

    @Builder
    public CreditEntry(CreditEntryId creditEntryId, CustomerId customerId, Money totalCreditAmount) {
        super.setId(creditEntryId);
        this.customerId = customerId;
        this.totalCreditAmount = totalCreditAmount;
    }

    public void addCreditAmount(Money amount) {
        totalCreditAmount = totalCreditAmount.add(amount);
    }

    public void subtractCreditAmount(Money amount) {
        totalCreditAmount = totalCreditAmount.subtract(amount);
    }
}
