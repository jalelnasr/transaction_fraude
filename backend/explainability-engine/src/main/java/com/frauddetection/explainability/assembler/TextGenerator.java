package com.frauddetection.explainability.assembler;

import com.frauddetection.common.dto.DecisionDTO;
import com.frauddetection.common.dto.ExplanationContextDTO;
import com.frauddetection.common.dto.InfluentialFeatureDTO;
import com.frauddetection.common.dto.TriggeredRuleDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class TextGenerator {

    public String generate(DecisionDTO decision) {
        ExplanationContextDTO context = decision.getExplanationContext();
        StringBuilder sb = new StringBuilder();

        sb.append(statusLabel(decision))
                .append(String.format(Locale.US, " (score fusionne : %.2f/1.00, %s).",
                        context.getFusedScore(), context.getDecidingThreshold()));

        List<TriggeredRuleDTO> triggeredRules = context.getTriggeredRules();
        if (triggeredRules != null && !triggeredRules.isEmpty()) {
            String rulesText = triggeredRules.stream()
                    .map(r -> String.format(Locale.US, "%s (poids %.2f) : %s", r.getRuleName(), r.getWeight(), r.getReason()))
                    .collect(Collectors.joining(" ; "));
            sb.append(" Regles declenchees : ").append(rulesText).append(".");
        }

        List<InfluentialFeatureDTO> influentialFeatures = context.getInfluentialFeatures();
        if (influentialFeatures != null && !influentialFeatures.isEmpty()) {
            String featuresText = influentialFeatures.stream()
                    .map(f -> String.format(Locale.US, "%s (%.2f)", f.getFeatureName(), f.getImportanceScore()))
                    .collect(Collectors.joining(", "));
            sb.append(" Variables les plus influentes selon le modele ML : ").append(featuresText).append(".");
        }

        return sb.toString();
    }

    private String statusLabel(DecisionDTO decision) {
        return switch (decision.getStatus()) {
            case BLOCKED -> "Transaction bloquee automatiquement";
            case MONITORED -> "Transaction placee sous surveillance";
            case ACCEPTED -> "Transaction acceptee";
        };
    }
}
