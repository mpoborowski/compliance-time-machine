package com.aquacode.ctm.audit.application;

import com.aquacode.ctm.audit.infrastructure.persistence.AuditRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.aquacode.ctm.audit.AuditFixtures.auditRecordEntity;
import static com.aquacode.ctm.audit.AuditFixtures.historicalTransactionAuditRecord;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditHistoryQueryServiceTest {

    @Mock
    private AuditRepository auditRepository;

    @Mock
    private AuditRecordMapper mapper;

    @InjectMocks
    private AuditHistoryQueryService auditHistoryQueryService;

    @Test
    void findTransaction_shouldReturnMappedHistoricalTransactionAuditRecordWhenAuditRecordExists() {
        var transactionId = "tx-1";
        var entity = auditRecordEntity();
        var historicalRecord = historicalTransactionAuditRecord();

        when(auditRepository.findByTransactionId(transactionId))
            .thenReturn(Optional.of(entity));
        when(mapper.toHistoricalTransactionAuditRecord(entity))
            .thenReturn(historicalRecord);

        var result = auditHistoryQueryService.findTransaction(transactionId);

        assertThat(result).contains(historicalRecord);

        verify(auditRepository).findByTransactionId(transactionId);
        verify(mapper).toHistoricalTransactionAuditRecord(entity);
    }

    @Test
    void findTransaction_shouldReturnEmptyWhenAuditRecordDoesNotExist() {
        var transactionId = "tx-missing";

        when(auditRepository.findByTransactionId(transactionId))
            .thenReturn(Optional.empty());

        var result = auditHistoryQueryService.findTransaction(transactionId);

        assertThat(result).isEmpty();

        verify(auditRepository).findByTransactionId(transactionId);
        verifyNoInteractions(mapper);
    }
}