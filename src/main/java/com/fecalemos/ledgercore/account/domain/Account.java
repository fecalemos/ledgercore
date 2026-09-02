package com.fecalemos.ledgercore.account.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entidade de domínio pura (sem Spring, sem jOOQ). balanceMinor = saldo em centavos.
 * version = usado para optimistic locking (evolução da Semana 3).
 */
public record Account (
    UUID id,
    String currency,
    long balanceMinor,
    long version,
    OffsetDateTime createdAt
) {}
