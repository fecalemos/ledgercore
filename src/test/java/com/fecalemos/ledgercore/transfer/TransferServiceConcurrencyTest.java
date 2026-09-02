package com.fecalemos.ledgercore.transfer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fecalemos.ledgercore.account.application.AccountService;
import com.fecalemos.ledgercore.account.domain.Account;
import com.fecalemos.ledgercore.account.infrastructure.AccountRepository;
import com.fecalemos.ledgercore.shared.Money;
import com.fecalemos.ledgercore.shared.error.DomainExceptions.InsufficientFundsException;
import com.fecalemos.ledgercore.transfer.application.TransferService;


@SpringBootTest
@Testcontainers
public class TransferServiceConcurrencyTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    AccountService accountService;
    @Autowired
    TransferService transferService;
    @Autowired
    AccountRepository accountRepository;

    @Test
    void concurrentTransfers_neverGoNegative_andConserveMoney() throws Exception {
        Account from = accountService.create("BRL");
        Account to = accountService.create("BRL");
        accountRepository.credit(from.id(), 300_00L);

        int threads = 50;
        ExecutorService pool = Executors.newFixedThreadPool(8); // < pool Hikari (10) p/ evitar espera por conexão
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger succeeded = new AtomicInteger();

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    transferService.transfer(from.id(), to.id(), Money.of("10.00", "BRL"));
                    succeeded.incrementAndGet();
                } catch (InsufficientFundsException expected) {
                    // esperado quando o saldo acaba
                } finally {
                    done.countDown();
                }
            });
        }

        assertThat(done.await(60, TimeUnit.SECONDS)).isTrue();
        pool.shutdown();

        long fromBalance = balanceOf(from.id());
        long toBalance = balanceOf(to.id());

        assertThat(fromBalance).isGreaterThanOrEqualTo(0);          // nunca negativo
        assertThat(fromBalance + toBalance).isEqualTo(300_00L);     // dinheiro conservado
        assertThat(succeeded.get()).isEqualTo(30);                  // exatamente 30 sucessos
        assertThat(toBalance).isEqualTo(300_00L);                   // 30 x R$10 = R$300
        assertThat(fromBalance).isEqualTo(0L);
    }

    private long balanceOf(UUID accountId) {
        return accountRepository.findById(accountId).balanceMinor();
    }
}
