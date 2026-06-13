package com.aquacode.ctm.history.application;

import java.time.Instant;

public class HistoricalDecisionUnreplayableException extends RuntimeException {

    public HistoricalDecisionUnreplayableException(String transactionId, Instant historicalTimestamp, Throwable cause) {
        super("Historical policy version for transaction %s at %s cannot be reconstructed"
                .formatted(transactionId, historicalTimestamp),
            cause
        );
    }
}