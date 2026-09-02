package com.fecalemos.ledgercore.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(long minor, String currency) {
    
    public Money {
        Objects.requireNonNull(currency, "currency");
        if (currency.length() != 3) {
            throw new IllegalArgumentException("currency deve ser um codigo ISO 4217 de 3 letras");
        }
    }

    public static Money of(String amount, String currency) {
        BigDecimal decimal;
        try {
            decimal = new BigDecimal(amount).setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("valor com mais de 2 casas decimais: " + amount);
        }
        return new Money(decimal.movePointRight(2).longValueExact(), currency);
    }

    public static Money ofMinor(long minor, String currency) {
        return new Money(minor, currency);
    }

    public long toMinor() {
        return minor;
    }

    public boolean isPositive() {
        return minor > 0;
    }
    
    public BigDecimal toBigDecimal() {
        return BigDecimal.valueOf(minor, 2);
    }

    @Override
    public String toString() {
        return toBigDecimal().toString() + " " + currency;
    }

}
