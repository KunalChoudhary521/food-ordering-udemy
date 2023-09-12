package com.food.ordering.payment.domain.valueobject;

import com.food.ordering.domain.valueobject.BaseId;

import java.util.UUID;

public class CreditEntryId extends BaseId<UUID> {

    public CreditEntryId(UUID value) {
        super(value);
    }
}
