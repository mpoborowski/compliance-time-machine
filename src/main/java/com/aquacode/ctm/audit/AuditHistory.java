package com.aquacode.ctm.audit;

import java.util.Optional;

public interface AuditHistory {

    Optional<HistoricalTransactionAuditRecord> findTransaction(String transactionId);
}