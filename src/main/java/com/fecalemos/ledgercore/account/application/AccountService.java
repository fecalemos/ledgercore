package com.fecalemos.ledgercore.account.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fecalemos.ledgercore.account.domain.Account;
import com.fecalemos.ledgercore.account.infrastructure.AccountRepository;

@Service
public class AccountService {
    private final AccountRepository accounts;

    public AccountService(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @Transactional()
    public Account create(String currency) {
        if (currency == null || currency.length() != 3) {
            throw new IllegalArgumentException("Currency must be a 3-letter ISO 4217 code");
        }
        return accounts.insert(currency.toUpperCase());
    }

    @Transactional(readOnly = true)
    public Account get(UUID id) {
        return accounts.findById(id);
    }
}
