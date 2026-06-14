package com.aquacode.ctm.audit.application;

import com.aquacode.ctm.ComplianceTimeMachineApplication;
import com.aquacode.ctm.TestcontainersConfiguration;
import com.aquacode.ctm.audit.infrastructure.persistence.AuditRecordEntity;
import com.aquacode.ctm.audit.infrastructure.persistence.AuditRepository;
import com.aquacode.ctm.evaluation.Decision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static com.aquacode.ctm.shared.DecisionMadeFixtures.decisionMadeEvent;

@SpringBootTest(classes = ComplianceTimeMachineApplication.class)
@Import(TestcontainersConfiguration.class)
class AuditEventIntegrationTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private static final Duration POLL_INTERVAL = Duration.ofMillis(100);

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private AuditRepository auditRepository;

    @BeforeEach
    void setUp() {
        auditRepository.deleteAll();
    }

    @Test
    void shouldPersistAuditRecordWhenDecisionMadeEventIsPublishedAfterTransactionCommit() {
        var event = decisionMadeEvent();

        transactionTemplate.executeWithoutResult(_ -> eventPublisher.publishEvent(event));

        var auditRecord = awaitAuditRecord("tx-1")
            .orElseThrow(() -> new AssertionError("Audit record was not persisted within timeout"));

        assertThat(auditRecord.id()).isNotNull();
        assertThat(auditRecord.transactionId()).isEqualTo("tx-1");
        assertThat(auditRecord.customerId()).isEqualTo("customer-1");
        assertThat(auditRecord.country()).isEqualTo("PL");
        assertThat(auditRecord.politicallyExposedPerson()).isFalse();
        assertThat(auditRecord.amount()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(auditRecord.transactionId()).isEqualTo("tx-1");
        assertThat(auditRecord.decisionId()).isEqualTo("dec_1");
        assertThat(auditRecord.ruleSetVersion()).isEqualTo("v1");
        assertThat(auditRecord.decision()).isEqualTo(Decision.APPROVED.name());
        assertThat(auditRecord.timestamp()).isEqualTo(Instant.parse("2025-01-01T12:00:00Z"));
    }

    private Optional<AuditRecordEntity> awaitAuditRecord(String transactionId) {
        var deadline = Instant.now().plus(TIMEOUT);

        while (Instant.now().isBefore(deadline)) {
            var auditRecord = findAuditRecord(transactionId);
            if (auditRecord.isPresent()) {
                return auditRecord;
            }
            sleep();
        }

        return findAuditRecord(transactionId);
    }

    private Optional<AuditRecordEntity> findAuditRecord(String transactionId) {
        return StreamSupport.stream(auditRepository.findAll().spliterator(), false)
            .filter(record -> transactionId.equals(record.transactionId()))
            .findFirst();
    }

    private static void sleep() {
        try {
            Thread.sleep(POLL_INTERVAL.toMillis());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for audit record", ex);
        }
    }
}