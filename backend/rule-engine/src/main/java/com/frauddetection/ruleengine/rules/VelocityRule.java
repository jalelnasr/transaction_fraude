package com.frauddetection.ruleengine.rules;

import com.frauddetection.common.dto.TransactionDTO;
import com.frauddetection.ruleengine.entity.Rule;
import com.frauddetection.ruleengine.enums.RuleType;
import com.frauddetection.ruleengine.repository.RuleExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VelocityRule implements RuleStrategy {

    private final RuleExecutionRepository ruleExecutionRepository;

    @Override
    public RuleType supports() {
        return RuleType.VELOCITY;
    }

    @Override
    public Optional<String> evaluate(TransactionDTO transaction, Rule rule) {
        Integer maxTransactions = rule.getMaxTransactionsPerWindow();
        Integer windowMinutes = rule.getWindowMinutes();
        if (maxTransactions == null || windowMinutes == null || transaction.getSourceAccountId() == null) {
            return Optional.empty();
        }

        Instant windowStart = Instant.now().minus(windowMinutes, ChronoUnit.MINUTES);
        long recentCount = ruleExecutionRepository.countBySourceAccountIdAndEvaluatedAtAfter(
                transaction.getSourceAccountId(), windowStart);

        if (recentCount >= maxTransactions) {
            return Optional.of("Frequence anormale: " + recentCount + " transactions sur le compte "
                    + transaction.getSourceAccountId() + " en " + windowMinutes + " minutes");
        }
        return Optional.empty();
    }
}
