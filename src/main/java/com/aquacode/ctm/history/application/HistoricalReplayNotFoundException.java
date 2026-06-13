package com.aquacode.ctm.history.application;

public class HistoricalReplayNotFoundException extends RuntimeException {

    public HistoricalReplayNotFoundException(String transactionId) {
        super("Historical transaction %s was not found".formatted(transactionId));
    }
}