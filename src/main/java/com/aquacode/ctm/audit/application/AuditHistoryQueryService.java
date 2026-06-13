package com.aquacode.ctm.audit.application;

import com.aquacode.ctm.audit.AuditHistory;
import com.aquacode.ctm.audit.HistoricalTransactionAuditRecord;
import com.aquacode.ctm.audit.infrastructure.persistence.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class AuditHistoryQueryService implements AuditHistory {

    private final AuditRepository auditRepository;
    private final AuditRecordMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<HistoricalTransactionAuditRecord> findTransaction(String transactionId) {
        return auditRepository.findByTransactionId(transactionId)
            .map(mapper::toHistoricalTransactionAuditRecord);
    }
}