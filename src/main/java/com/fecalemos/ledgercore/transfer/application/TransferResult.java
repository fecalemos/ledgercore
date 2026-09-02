package com.fecalemos.ledgercore.transfer.application;

import java.util.UUID;

public record TransferResult(UUID transferId, String status) {
    public static TransferResult completed(UUID transferId) {
        return new TransferResult(transferId, "COMPLETED");
    }
}
