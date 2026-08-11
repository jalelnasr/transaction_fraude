package com.frauddetection.explainability.assembler;

import com.frauddetection.common.dto.DecisionDTO;
import com.frauddetection.common.dto.ExplanationContextDTO;
import com.frauddetection.common.dto.InfluentialFeatureDTO;
import com.frauddetection.common.dto.TriggeredRuleDTO;
import com.frauddetection.common.enums.DecisionType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TextGeneratorTests {

    private final TextGenerator textGenerator = new TextGenerator();

    @Test
    void generatesTextWithTriggeredRulesAndInfluentialFeatures() {
        ExplanationContextDTO context = ExplanationContextDTO.builder()
                .fusedScore(0.8)
                .decidingThreshold("block-threshold(0.85)")
                .triggeredRules(List.of(
                        TriggeredRuleDTO.builder()
                                .ruleName("Montant eleve")
                                .weight(0.4)
                                .reason("Montant superieur au seuil")
                                .build()))
                .influentialFeatures(List.of(
                        InfluentialFeatureDTO.builder()
                                .featureName("amount")
                                .importanceScore(0.19)
                                .build()))
                .build();

        DecisionDTO decision = DecisionDTO.builder()
                .transactionId("tx-1")
                .status(DecisionType.BLOCKED)
                .fusedScore(0.8)
                .explanationContext(context)
                .build();

        String text = textGenerator.generate(decision);

        assertThat(text).contains("bloquee");
        assertThat(text).contains("Montant eleve");
        assertThat(text).contains("amount");
    }

    @Test
    void generatesTextEvenWithoutTriggeredRulesOrFeatures() {
        ExplanationContextDTO context = ExplanationContextDTO.builder()
                .fusedScore(0.6)
                .decidingThreshold("monitor-threshold(0.5)")
                .triggeredRules(List.of())
                .influentialFeatures(List.of())
                .build();

        DecisionDTO decision = DecisionDTO.builder()
                .transactionId("tx-2")
                .status(DecisionType.MONITORED)
                .fusedScore(0.6)
                .explanationContext(context)
                .build();

        String text = textGenerator.generate(decision);

        assertThat(text).contains("surveillance");
    }
}
