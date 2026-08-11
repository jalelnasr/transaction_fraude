package com.frauddetection.ruleengine.service;

import com.frauddetection.ruleengine.entity.Rule;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScoreCalculationService {

    public double computeRuleScore(List<Rule> activeRules, List<Rule> triggeredRules) {
        if (activeRules.isEmpty()) {
            return 0.0;
        }
        double totalWeight = activeRules.stream().mapToDouble(Rule::getWeight).sum();
        if (totalWeight <= 0) {
            return 0.0;
        }
        double triggeredWeight = triggeredRules.stream().mapToDouble(Rule::getWeight).sum();
        return Math.min(1.0, triggeredWeight / totalWeight);
    }
}
