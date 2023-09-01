package com.food.ordering.domain.valueobject;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Money implements Comparable<BigDecimal> {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    private final BigDecimal amount;

    public boolean isGreaterThanZero() {
        return amount != null && compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isGreaterThan(Money money) {
        return amount != null && compareTo(money.amount) > 0;
    }

    public Money add(Money money) {
        BigDecimal sum = setScale(amount.add(money.amount));
        return new Money(sum);
    }

    public Money subtract(Money money) {
        BigDecimal difference = setScale(amount.subtract(money.amount));
        return new Money(difference);
    }

    public Money multiply(int multiplier) {
        BigDecimal product = setScale(amount.multiply(new BigDecimal(multiplier)));
        return new Money(product);
    }

    @Override
    public int compareTo(BigDecimal other) {
        return amount.compareTo(other);
    }

    private BigDecimal setScale(BigDecimal input) {
        // RoundingMode.HALF_EVEN - https://medium.com/@ali.gelenler/using-fractional-numbers-in-java-d070f1b9b8c5
        return input.setScale(2, RoundingMode.HALF_EVEN);
    }
}
