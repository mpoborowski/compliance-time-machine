package com.aquacode.ctm.evaluation.infrastructure;

import com.aquacode.ctm.evaluation.application.EvaluationService;
import com.aquacode.ctm.evaluation.infrastructure.dto.EvaluateTransactionRequest;
import com.aquacode.ctm.evaluation.infrastructure.dto.EvaluateTransactionResponse;
import com.aquacode.ctm.evaluation.application.EvaluationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final EvaluationMapper mapper;

    @PostMapping
    EvaluateTransactionResponse evaluate(@Valid @RequestBody EvaluateTransactionRequest request) {
        var decision  = evaluationService.evaluate(mapper.fromEvaluationRequest(request));
        return mapper.fromComplianceDecision(decision);
    }
}
