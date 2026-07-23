-- Contas: saldo em unidade menor (centavos) para precisão monetária.
-- version = optimistic locking (evita lost update em transferências concorrentes).

CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    currency CHAR(3) NOT NULL,
    balance_minor BIGINT NOT NULL DEFAULT 0 CHECK (balance_minor >= 0),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);