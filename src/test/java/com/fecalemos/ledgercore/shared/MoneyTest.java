package com.fecalemos.ledgercore.shared;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MoneyTest {

    @Test
    void of_convertsDecimalStringToMinorUnits() {
        assertThat(Money.of("100.00", "BRL").toMinor()).isEqualTo(10_000l);
        assertThat(Money.of("100", "BRL").toMinor()).isEqualTo(10_000l);
        assertThat(Money.of("0.01", "USD").toMinor()).isEqualTo(1L);
    }

    @Test
    void toDecimal_roundTripsBack() {
        assertThat(Money.ofMinor(10_000l, "BRL").toBigDecimal().toPlainString()).isEqualTo("100.00");
        assertThat(Money.ofMinor(1L, "USD").toBigDecimal().toPlainString()).isEqualTo("0.01");
    }

    @Test
    void of_rejectsMoreThanTwoDecimaPlaces() {
        assertThatThrownBy(() -> Money.of("100.005", "BRL")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void constructor_rejectsInvalidCurrencyCode() {
        assertThatThrownBy(() -> Money.ofMinor(100L, "REAL")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void isPositive_reflectsSign() {
        assertThat(Money.ofMinor(100L, "BRL").isPositive()).isTrue();
        assertThat(Money.ofMinor(0L, "BRL").isPositive()).isFalse();
    }

}
