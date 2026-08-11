package com.frauddetection.ruleengine.rules;

import com.frauddetection.common.dto.TransactionDTO;
import com.frauddetection.ruleengine.entity.Rule;
import com.frauddetection.ruleengine.enums.RuleType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class AmountThresholdRule implements RuleStrategy {

    @Override
    public RuleType supports() {
        return RuleType.AMOUNT_THRESHOLD;
    }

    @Override
    public Optional<String> evaluate(TransactionDTO transaction, Rule rule) {
        BigDecimal threshold = rule.getThresholdAmount();
        if (threshold == null || transaction.getAmount() == null) {
            return Optional.empty();
        }
        if (transaction.getAmount().compareTo(threshold) > 0) {
            return Optional.of("Montant " + transaction.getAmount() + " " + transaction.getCurrency()
                    + " superieur au seuil " + threshold);
        }
        return Optional.empty();
    }
}
