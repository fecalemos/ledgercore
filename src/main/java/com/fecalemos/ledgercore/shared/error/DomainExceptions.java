package com.fecalemos.ledgercore.shared.error;

import java.util.UUID;

public final class DomainExceptions {

    private DomainExceptions() {}

    /** 404 - Account Not Found */
    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(UUID id) {
            super("Account not found with id: " + id);
        }
    }

    /** 409 -  saldo insuficiente para a transferência */
    public static class InsufficientFundsException extends RuntimeException {
        public InsufficientFundsException(UUID accountId) {
            super("Saldo insuficiente na conta: " + accountId);
        }
    }

    /** 422 - moedas incompativeis (sem conversao FX no MVP). */
    public static class CurrencyMismatchException extends RuntimeException {
        public CurrencyMismatchException(String expected, String actual) {
            super("Moeda incompatível: esperado " + expected + ", recebido " + actual);
        }
    }
}
