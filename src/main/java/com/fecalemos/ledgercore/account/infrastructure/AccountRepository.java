package com.fecalemos.ledgercore.account.infrastructure;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.field;

import com.fecalemos.ledgercore.account.domain.Account;
import com.fecalemos.ledgercore.shared.error.DomainExceptions.AccountNotFoundException;

/**
 * Acesso a dados de contas com jOOQ.
 *
 * Aqui usamos referências por NOME (table("accounts"), field("balance_minor")) para que o
 * projeto compile mesmo antes de rodar `./gradlew generateJooq`. Depois de gerar as classes,
 * você pode trocar por versões tipadas, ex.:
 *   import static com.fernandolemos.ledgercore.jooq.Tables.ACCOUNTS;
 *   dsl.selectFrom(ACCOUNTS).where(ACCOUNTS.ID.eq(id)) ...
 */
@Repository
public class AccountRepository {
    private static final Table<Record> ACCOUNTS = table("accounts");
    private static final Field<UUID> ID = field("id", UUID.class);
    private static final Field<String> CURRENCY = field("currency", String.class);
    private static final Field<Long> BALANCE_MINOR = field("balance_minor", Long.class);
    private static final Field<Long> VERSION = field("version", Long.class);
    private static final Field<OffsetDateTime> CREATED_AT = field("created_at", OffsetDateTime.class);


    private final DSLContext dsl;

    public AccountRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Account insert(String currency) {
        UUID id = UUID.randomUUID();
        dsl.insertInto(ACCOUNTS)
            .set(ID, id)
            .set(CURRENCY, currency)
            .set(BALANCE_MINOR, 0L)
            .set(VERSION, 0L)
            .execute();
        return findById(id);
    }

    public Account findById(UUID id) {
        Record r = dsl.select(ID, CURRENCY, BALANCE_MINOR, VERSION, CREATED_AT)
            .from(ACCOUNTS)
            .where(ID.eq(id))
            .fetchOne();
        if (r == null) {
            throw new AccountNotFoundException(id);
        }
        return map(r);
    }

    public Account findByIdForUpdate(UUID id) {
        Record r = dsl.select(ID, CURRENCY, BALANCE_MINOR, VERSION, CREATED_AT)
            .from(ACCOUNTS)
            .where(ID.eq(id))
            .forUpdate()
            .fetchOne();
        if (r == null) throw new AccountNotFoundException(id);
        return map(r);
    }
    
    public void credit(UUID id, long amountMinor) {
        dsl.update(ACCOUNTS)
            .set(BALANCE_MINOR, BALANCE_MINOR.plus(amountMinor))
            .set(VERSION, VERSION.plus(1L))
            .where(ID.eq(id))
            .execute();
    }

    public void debit(UUID id, long amountMinor) {
        dsl.update(ACCOUNTS)
            .set(BALANCE_MINOR, BALANCE_MINOR.minus(amountMinor))
            .set(VERSION, VERSION.plus(1L))
            .where(ID.eq(id))
            .execute();
    }

    private Account map(Record r) {
        return new Account(
            r.get(ID),
            r.get(CURRENCY),
            r.get(BALANCE_MINOR),
            r.get(VERSION),
            r.get(CREATED_AT)
        );
    }
}
