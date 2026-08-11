package com.frauddetection.ruleengine.service;

import com.frauddetection.common.dto.RuleScoreDTO;
import com.frauddetection.common.dto.TransactionDTO;
import com.frauddetection.common.dto.TriggeredRuleDTO;
import com.frauddetection.ruleengine.entity.Rule;
import com.frauddetection.ruleengine.entity.RuleExecution;
import com.frauddetection.ruleengine.enums.RuleType;
import com.frauddetection.ruleengine.repository.RuleExecutionRepository;
import com.frauddetection.ruleengine.repository.RuleRepository;
import com.frauddetection.ruleengine.rules.RuleStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RuleEvaluationService {

    private final RuleRepository ruleRepository;
    private final RuleExecutionRepository ruleExecutionRepository;
    private final ScoreCalculationService scoreCalculationService;
    private final Map<RuleType, RuleStrategy> strategiesByType;

    public RuleEvaluationService(RuleRepository ruleRepository,
                                  RuleExecutionRepository ruleExecutionRepository,
                                  ScoreCalculationService scoreCalculationService,
                                  List<RuleStrategy> strategies) {
        this.ruleRepository = ruleRepository;
        this.ruleExecutionRepository = ruleExecutionRepository;
        this.scoreCalculationService = scoreCalculationService;
        this.strategiesByType = strategies.stream()
                .collect(Collectors.toMap(RuleStrategy::supports, Function.identity()));
    }

    public RuleScoreDTO evaluate(TransactionDTO transaction) {
        List<Rule> activeRules = ruleRepository.findByActiveTrue();
        List<Rule> triggeredRuleEntities = new ArrayList<>();
        List<TriggeredRuleDTO> triggeredRuleDTOs = new ArrayList<>();

        for (Rule rule : activeRules) {
            RuleStrategy strategy = strategiesByType.get(rule.getType());
            if (strategy == null) {
                log.warn("No strategy registered for rule type [{}], skipping rule [{}]", rule.getType(), rule.getId());
                continue;
            }

            Optional<String> triggered = strategy.evaluate(transaction, rule);
            if (triggered.isPresent()) {
                triggeredRuleEntities.add(rule);
                triggeredRuleDTOs.add(TriggeredRuleDTO.builder()
                        .ruleId(rule.getId())
                        .ruleName(rule.getName())
                        .weight(rule.getWeight())
                        .reason(triggered.get())
                        .build());
            }
        }

        double ruleScore = scoreCalculationService.computeRuleScore(activeRules, triggeredRuleEntities);

        ruleExecutionRepository.save(RuleExecution.builder()
                .id(UUID.randomUUID().toString())
                .transactionId(transaction.getTransactionId())
                .sourceAccountId(transaction.getSourceAccountId())
                .ruleScore(ruleScore)
                .evaluatedAt(Instant.now())
                .build());

        return RuleScoreDTO.builder()
                .transactionId(transaction.getTransactionId())
                .ruleScore(ruleScore)
                .triggeredRules(triggeredRuleDTOs)
                .evaluatedAt(Instant.now())
                .build();
    }
}
