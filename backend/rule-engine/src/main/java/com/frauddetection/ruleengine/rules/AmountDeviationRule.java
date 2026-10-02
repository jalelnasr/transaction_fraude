package com.frauddetection.ruleengine.rules;

import com.frauddetection.common.dto.TransactionDTO;
import com.frauddetection.ruleengine.entity.Rule;
import com.frauddetection.ruleengine.enums.RuleType;
import com.frauddetection.ruleengine.repository.RuleExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AmountDeviationRule implements RuleStrategy {

    private final RuleExecutionRepository ruleExecutionRepository;

    @Override
    public RuleType supports() {
        return RuleType.AMOUNT_DEVIATION;
    }

    @Override
    public Optional<String> evaluate(TransactionDTO transaction, Rule rule) {
        Double multiplier = rule.getDeviationMultiplier();
        if (multiplier == null || multiplier <= 0 || transaction.getSourceAccountId() == null
                || transaction.getAmount() == null) {
            return Optional.empty();
        }

        Double avgAmount = ruleExecutionRepository.avgAmountBySourceAccountId(transaction.getSourceAccountId());
        if (avgAmount == null || avgAmount <= 0) {
            return Optional.empty();
        }

        BigDecimal threshold = BigDecimal.valueOf(avgAmount * multiplier);
        if (transaction.getAmount().compareTo(threshold) > 0) {
            return Optional.of(String.format(
                    "Montant %.2f superieur a %.1fx la moyenne habituelle du client (%.2f)",
                    transaction.getAmount().doubleValue(), multiplier, avgAmount));
        }
        return Optional.empty();
    }
}
