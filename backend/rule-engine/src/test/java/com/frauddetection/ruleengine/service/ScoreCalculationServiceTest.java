package com.frauddetection.ruleengine.service;

import com.frauddetection.ruleengine.entity.Rule;
import com.frauddetection.ruleengine.enums.RuleType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class ScoreCalculationServiceTest {

    private final ScoreCalculationService service = new ScoreCalculationService();

    @Test
    void returnsZeroWhenNoActiveRules() {
        assertThat(service.computeRuleScore(List.of(), List.of())).isEqualTo(0.0);
    }

    @Test
    void returnsZeroWhenNoRuleTriggered() {
        Rule amountRule = Rule.builder().type(RuleType.AMOUNT_THRESHOLD).weight(0.4).build();
        Rule geoRule = Rule.builder().type(RuleType.GEOLOCATION).weight(0.3).build();

        double score = service.computeRuleScore(List.of(amountRule, geoRule), List.of());

        assertThat(score).isEqualTo(0.0);
    }

    @Test
    void computesProportionalScoreForTriggeredRules() {
        Rule amountRule = Rule.builder().type(RuleType.AMOUNT_THRESHOLD).weight(0.4).build();
        Rule geoRule = Rule.builder().type(RuleType.GEOLOCATION).weight(0.6).build();

        double score = service.computeRuleScore(List.of(amountRule, geoRule), List.of(amountRule));

        assertThat(score).isCloseTo(0.4, within(0.0001));
    }

    @Test
    void scoreNeverExceedsOne() {
        Rule rule = Rule.builder().type(RuleType.AMOUNT_THRESHOLD).weight(1.0).build();

        double score = service.computeRuleScore(List.of(rule), List.of(rule, rule));

        assertThat(score).isEqualTo(1.0);
    }
}
