-- Idempotência: a mesma Idempotency-Key sempre devolve a mesma resposta,
-- sem reexecutar o efeito colateral (padrão de APIs de pagamento reais).
CREATE TABLE idempotency_keys (
    key           VARCHAR(255) PRIMARY KEY,
    response_body JSONB        NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);
 