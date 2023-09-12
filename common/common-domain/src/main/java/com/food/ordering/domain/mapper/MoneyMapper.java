package com.food.ordering.domain.mapper;

import com.food.ordering.domain.valueobject.Money;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper
public interface MoneyMapper {

    default Money toMoney(BigDecimal bigDecimal) {// TODO: Refactor to automatic mapping
        // multiple to set precision
        return new Money(bigDecimal).multiply(1);
    }

    default BigDecimal toAmount(Money money) {
        return money.getAmount();
    }
}
