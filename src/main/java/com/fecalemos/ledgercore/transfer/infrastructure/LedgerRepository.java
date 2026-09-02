package com.fecalemos.ledgercore.transfer.infrastructure;

import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import org.springframework.stereotype.Repository;

@Repository
public class LedgerRepository {

    private static final Table<Record> LEDGER_ENTRIES = table("ledger_entries");
    private static final Field<UUID> ID = field("id", UUID.class);
    private static final Field<UUID> TRANSFER_ID = field("transfer_id", UUID.class);
    private static final Field<UUID> ACCOUNT_ID = field("account_id", UUID.class);
    private static final Field<Long> AMOUNT_MINOR = field("amount_minor", Long.class);
    private static final Field<String> CURRENCY = field("currency", String.class);

    private final DSLContext dsl;

    public LedgerRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public void insertEntry(UUID transferId, UUID accountId, Long amountMinor, String currency) {
        UUID id = UUID.randomUUID();
        dsl.insertInto(LEDGER_ENTRIES)
            .set(ID, id)
            .set(TRANSFER_ID, transferId)
            .set(ACCOUNT_ID, accountId)
            .set(AMOUNT_MINOR, amountMinor)
            .set(CURRENCY, currency)
            .execute();
    }
}
