-- Razão de dupla entrada (append-only, imutável).
-- amount_minor: positivo = crédito, negativo = débito.
-- Invariante de negócio: SUM(amount_minor) por transfer_id = 0.

CREATE TABLE ledger_entries (
    id UUID PRIMARY KEY,
    transfer_id UUID NOT NULL,
    account_id UUID NOT NULL,
    amount_minor BIGINT NOT NULL,
    currency CHAR(3) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
