package com.frauddetection.explainability.service;

import com.frauddetection.common.dto.DecisionDTO;
import com.frauddetection.common.dto.ExplanationDTO;
import com.frauddetection.common.enums.DecisionType;
import com.frauddetection.explainability.assembler.TextGenerator;
import com.frauddetection.explainability.entity.Explanation;
import com.frauddetection.explainability.kafka.ExplanationProducer;
import com.frauddetection.explainability.repository.ExplanationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExplanationService {

    private final TextGenerator textGenerator;
    private final ExplanationRepository explanationRepository;
    private final ExplanationProducer explanationProducer;

    @Transactional
    public void handleDecision(DecisionDTO decision) {
        if (decision.getStatus() == DecisionType.ACCEPTED) {
            log.debug("No explanation needed for accepted transaction [{}]", decision.getTransactionId());
            return;
        }

        String justification = textGenerator.generate(decision);
        Instant now = Instant.now();

        Explanation explanation = Explanation.builder()
                .id(UUID.randomUUID().toString())
                .transactionId(decision.getTransactionId())
                .justification(justification)
                .generatedAt(now)
                .build();
        explanationRepository.save(explanation);

        ExplanationDTO explanationDTO = ExplanationDTO.builder()
                .transactionId(decision.getTransactionId())
                .justification(justification)
                .generatedAt(now)
                .build();
        explanationProducer.publish(explanationDTO);
    }
}
