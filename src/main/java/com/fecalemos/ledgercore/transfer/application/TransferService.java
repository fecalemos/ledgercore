package com.fecalemos.ledgercore.transfer.application;

import org.springframework.stereotype.Service;

import com.fecalemos.ledgercore.account.domain.Account;
import com.fecalemos.ledgercore.account.infrastructure.AccountRepository;
import com.fecalemos.ledgercore.shared.Money;
import com.fecalemos.ledgercore.shared.error.DomainExceptions.CurrencyMismatchException;
import com.fecalemos.ledgercore.shared.error.DomainExceptions.InsufficientFundsException;
import com.fecalemos.ledgercore.transfer.infrastructure.LedgerRepository;

import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;
/**
 * Núcleo do LedgerCore: transferência atômica, thread-safe e de dupla entrada.
 *
 * Garantias:
 *  - ATOMICIDADE: tudo roda numa única transação (@Transactional). Falhou → rollback total.
 *  - THREAD-SAFETY: as duas contas são travadas com SELECT ... FOR UPDATE. Travamos SEMPRE
 *    na mesma ordem (por UUID) para evitar deadlock entre transferências cruzadas (A→B e B→A).
 *  - DUPLA ENTRADA: cada transferência gera dois lançamentos que somam zero.
 *  - DINHEIRO NÃO SOME: débito e crédito acontecem juntos ou não acontecem.
 */
@Service
public class TransferService {
    private final AccountRepository accounts;
    private final LedgerRepository ledger;

    public TransferService(AccountRepository accounts, LedgerRepository ledger) {
        this.accounts = accounts;
        this.ledger = ledger;
    }

    @Transactional
    public TransferResult transfer(UUID fromId, UUID toId, Money amount) {
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("The source and destination accounts cannot be the same");
        }
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("The transfer amount must be positive");
        }

       
        // Sempre a mesma ordem de lock (UUID crescente) evita deadlock A→B vs B→A.
        UUID firstId = fromId.compareTo(toId) < 0 ? fromId : toId;
        UUID secondId = firstId.equals(fromId) ? toId : fromId;
        accounts.findByIdForUpdate(firstId);
        accounts.findByIdForUpdate(secondId);

        Account from = accounts.findById(fromId);
        Account to = accounts.findById(toId);

        if (!from.currency().equals(amount.currency())) {
            throw new CurrencyMismatchException(from.currency(), amount.currency());
        }
        if (!to.currency().equals(amount.currency())) {
            throw new CurrencyMismatchException(to.currency(), amount.currency());
        }
        if (from.balanceMinor() < amount.toMinor()) {
            throw new InsufficientFundsException(fromId);
        }

        long value = amount.toMinor();
        UUID transferId = UUID.randomUUID();


        accounts.debit(fromId, value);
        accounts.credit(toId, value);

        ledger.insertEntry(transferId, fromId, -value, amount.currency());
        ledger.insertEntry(transferId, toId, +value, amount.currency());

        return TransferResult.completed(transferId);
    }
}
