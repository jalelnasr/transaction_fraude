package com.frauddetection.decision.controller;

import com.frauddetection.common.exceptions.ServiceException;
import com.frauddetection.decision.dto.DecisionResponse;
import com.frauddetection.decision.entity.FraudDecision;
import com.frauddetection.decision.repository.DecisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class DecisionController {

    private final DecisionRepository decisionRepository;

    @GetMapping("/{id}/decision")
    public ResponseEntity<DecisionResponse> getDecision(@PathVariable("id") String transactionId) {
        FraudDecision decision = decisionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ServiceException("No decision found for transaction: " + transactionId));

        return ResponseEntity.ok(DecisionResponse.builder()
                .transactionId(decision.getTransactionId())
                .status(decision.getStatus())
                .fusedScore(decision.getFusedScore())
                .ruleScore(decision.getRuleScore())
                .mlScore(decision.getMlScore())
                .degradedMode(decision.isDegradedMode())
                .decidedAt(decision.getDecidedAt())
                .build());
    }
}
