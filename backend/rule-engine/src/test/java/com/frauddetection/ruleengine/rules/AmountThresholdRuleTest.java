package com.frauddetection.ruleengine.rules;

import com.frauddetection.common.dto.TransactionDTO;
import com.frauddetection.ruleengine.entity.Rule;
import com.frauddetection.ruleengine.enums.RuleType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AmountThresholdRuleTest {

    private final AmountThresholdRule rule = new AmountThresholdRule();

    @Test
    void triggersWhenAmountExceedsThreshold() {
        Rule config = Rule.builder().thresholdAmount(new BigDecimal("5000")).build();
        TransactionDTO transaction = TransactionDTO.builder()
                .amount(new BigDecimal("7500"))
                .currency("TND")
                .build();

        Optional<String> result = rule.evaluate(transaction, config);

        assertThat(result).isPresent();
        assertThat(result.get()).contains("7500");
    }

    @Test
    void doesNotTriggerWhenAmountBelowThreshold() {
        Rule config = Rule.builder().thresholdAmount(new BigDecimal("5000")).build();
        TransactionDTO transaction = TransactionDTO.builder()
                .amount(new BigDecimal("100"))
                .currency("TND")
                .build();

        Optional<String> result = rule.evaluate(transaction, config);

        assertThat(result).isEmpty();
    }

    @Test
    void supportsAmountThresholdType() {
        assertThat(rule.supports()).isEqualTo(RuleType.AMOUNT_THRESHOLD);
    }
}
