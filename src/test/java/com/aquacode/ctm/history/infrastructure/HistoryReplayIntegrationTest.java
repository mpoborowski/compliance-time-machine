package com.aquacode.ctm.history.infrastructure;

import com.aquacode.ctm.ComplianceTimeMachineApplication;
import com.aquacode.ctm.TestcontainersConfiguration;
import com.aquacode.ctm.audit.infrastructure.persistence.AuditRepository;
import com.aquacode.ctm.rules.infrastructure.persistence.RuleDefinitionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ComplianceTimeMachineApplication.class)
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class HistoryReplayIntegrationTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private static final Duration POLL_INTERVAL = Duration.ofMillis(100);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        auditRepository.deleteAll();
        jdbcTemplate.update("DELETE FROM rule_definition_entity");
        jdbcTemplate.update("DELETE FROM rule_set_entity");
    }

    @Test
    void replay_shouldReturnHistoricalDecisionForPreviouslyEvaluatedTransaction() throws Exception {
        givenRuleSet();

        mockMvc.perform(post("/api/v1/evaluations")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "transactionId": "TX-123",
                      "customerId": "customer-1",
                      "country": "PL",
                      "amount": 10.00,
                      "politicallyExposedPerson": true,
                      "transactionTimestamp": "2025-03-01T10:00:00Z"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.decision").value("REJECTED"))
            .andExpect(jsonPath("$.appliedRuleSetVersion").value("2025-Q1"));

        awaitAuditRecord("TX-123");

        mockMvc.perform(get("/api/v1/history/transactions/{transactionId}", "TX-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.transactionId").value("TX-123"))
            .andExpect(jsonPath("$.historicalTimestamp").exists())
            .andExpect(jsonPath("$.ruleSetVersion").value("2025-Q1"))
            .andExpect(jsonPath("$.decision").value("REJECTED"))
            .andExpect(jsonPath("$.rules").isArray())
            .andExpect(jsonPath("$.rules.length()").value(3))
            .andExpect(jsonPath("$.rules[0].ruleCode").value("AML-001"))
            .andExpect(jsonPath("$.rules[0].version").value("v1"))
            .andExpect(jsonPath("$.rules[0].outcome").value("PASS"))
            .andExpect(jsonPath("$.rules[0].explanation").value("Transaction amount 10.0000 is within threshold 100.0"))
            .andExpect(jsonPath("$.rules[1].ruleCode").value("COUNTRY-001"))
            .andExpect(jsonPath("$.rules[1].version").value("v1"))
            .andExpect(jsonPath("$.rules[1].outcome").value("PASS"))
            .andExpect(jsonPath("$.rules[1].explanation").value("Country PL classified as non-high risk"))
            .andExpect(jsonPath("$.rules[2].ruleCode").value("PEP-001"))
            .andExpect(jsonPath("$.rules[2].version").value("v1"))
            .andExpect(jsonPath("$.rules[2].outcome").value("FAIL"))
            .andExpect(jsonPath("$.rules[2].explanation").value("Customer identified as PEP"))
            .andExpect(jsonPath("$.replayedAt", notNullValue()));
    }

    @Test
    void replay_shouldNotCreateAdditionalAuditDecisionRecord() throws Exception {
        givenRuleSet();

        mockMvc.perform(post("/api/v1/evaluations")
                .contentType(APPLICATION_JSON)
                .content("""
                    {
                      "transactionId": "TX-456",
                      "customerId": "customer-2",
                      "country": "PL",
                      "amount": 10.00,
                      "politicallyExposedPerson": true,
                      "transactionTimestamp": "2025-03-01T10:00:00Z"
                    }
                    """))
            .andExpect(status().isOk());

        awaitAuditRecord("TX-456");

        var auditRecordCountBeforeReplay = auditRecordCount();

        mockMvc.perform(get("/api/v1/history/transactions/{transactionId}", "TX-456"))
            .andExpect(status().isOk());

        var auditRecordCountAfterReplay = auditRecordCount();

        assertThat(auditRecordCountAfterReplay).isEqualTo(auditRecordCountBeforeReplay);
        assertThat(auditRecordCountAfterReplay).isEqualTo(1);
    }

    private void givenRuleSet() {
        var ruleSetId = UUID.randomUUID();

        jdbcTemplate.update("""
                INSERT INTO rule_set_entity (
                    id,
                    version,
                    effective_from,
                    effective_to
                )
                VALUES (?, ?, ?, ?)
                """,
            ruleSetId,
            "2025-Q1",
            OffsetDateTime.ofInstant(Instant.parse("2020-01-01T00:00:00Z"), ZoneOffset.UTC),
            null
        );

        insertRuleDefinition(
            ruleSetId,
            "AML-001",
            "v1",
            "Amount threshold rule",
            RuleDefinitionType.AMOUNT_THRESHOLD,
            10,
            true,
            """
            {"threshold": 100.00}
            """
        );

        insertRuleDefinition(
            ruleSetId,
            "COUNTRY-001",
            "v1",
            "High risk country rule",
            RuleDefinitionType.HIGH_RISK_COUNTRY,
            20,
            true,
            """
            {"countries": ["RU", "KP"]}
            """
        );

        insertRuleDefinition(
            ruleSetId,
            "PEP-001",
            "v1",
            "PEP rule",
            RuleDefinitionType.PEP,
            30,
            true,
            "{}"
        );
    }

    private void insertRuleDefinition(
        UUID ruleSetId,
        String code,
        String version,
        String description,
        RuleDefinitionType type,
        int priority,
        boolean enabled,
        String configuration
    ) {
        jdbcTemplate.update("""
                INSERT INTO rule_definition_entity (
                    id,
                    rule_set_id,
                    code,
                    version,
                    description,
                    type,
                    priority,
                    enabled,
                    configuration
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, CAST(? AS jsonb))
                """,
            UUID.randomUUID(),
            ruleSetId,
            code,
            version,
            description,
            type.name(),
            priority,
            enabled,
            configuration
        );
    }

    private void awaitAuditRecord(String transactionId) {
        var deadline = Instant.now().plus(TIMEOUT);

        while (Instant.now().isBefore(deadline)) {
            var exists = StreamSupport.stream(auditRepository.findAll().spliterator(), false)
                .anyMatch(record -> transactionId.equals(record.transactionId()));

            if (exists) {
                return;
            }

            sleep();
        }

        throw new AssertionError("Audit record was not persisted within timeout for transaction " + transactionId);
    }

    private long auditRecordCount() {
        return StreamSupport.stream(auditRepository.findAll().spliterator(), false)
            .count();
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