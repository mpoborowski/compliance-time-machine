package com.aquacode.ctm.audit.application;

import com.aquacode.ctm.audit.HistoricalTransactionAuditRecord;
import com.aquacode.ctm.audit.infrastructure.persistence.AuditRecordEntity;
import com.aquacode.ctm.audit.infrastructure.persistence.AuditRepository;
import com.aquacode.ctm.evaluation.Decision;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

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
        var entity = givenAuditRecordEntity();
        var historicalRecord = givenHistoricalTransactionAuditRecord();

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

    private static AuditRecordEntity givenAuditRecordEntity() {
        return AuditRecordEntity.builder()
            .id(UUID.fromString("00000000-0000-0000-0000-000000000001"))
            .transactionId("tx-1")
            .customerId("customer-1")
            .country("PL")
            .amount(BigDecimal.TEN)
            .politicallyExposedPerson(false)
            .transactionTimestamp(Instant.parse("2025-01-01T00:00:00Z"))
            .decisionId("dec-1")
            .ruleSetVersion("v1")
            .decision(Decision.APPROVED.name())
            .timestamp(Instant.parse("2025-01-01T12:00:00Z"))
            .build();
    }

    private static HistoricalTransactionAuditRecord givenHistoricalTransactionAuditRecord() {
        return new HistoricalTransactionAuditRecord(
            "tx-1",
            "customer-1",
            "PL",
            BigDecimal.TEN,
            false,
            Instant.parse("2025-01-01T00:00:00Z"),
            "dec-1",
            "v1",
            Decision.APPROVED,
            Instant.parse("2025-01-01T12:00:00Z")
        );
    }
}