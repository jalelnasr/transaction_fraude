package com.frauddetection.decision.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ScoreFusionService {

    @Value("${decision.rule-weight:0.4}")
    private double ruleWeight;

    @Value("${decision.ml-weight:0.6}")
    private double mlWeight;

    public double fuse(double ruleScore, double mlScore) {
        return Math.min(1.0, (ruleWeight * ruleScore) + (mlWeight * mlScore));
    }

    public double getRuleWeight() {
        return ruleWeight;
    }

    public double getMlWeight() {
        return mlWeight;
    }
}
