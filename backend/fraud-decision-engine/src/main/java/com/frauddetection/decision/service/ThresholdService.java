package com.frauddetection.decision.service;

import com.frauddetection.common.enums.DecisionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ThresholdService {

    @Value("${decision.monitor-threshold:0.5}")
    private double monitorThreshold;

    @Value("${decision.block-threshold:0.85}")
    private double blockThreshold;

    public DecisionType decide(double fusedScore) {
        if (fusedScore >= blockThreshold) {
            return DecisionType.BLOCKED;
        }
        if (fusedScore >= monitorThreshold) {
            return DecisionType.MONITORED;
        }
        return DecisionType.ACCEPTED;
    }

    public String describeThreshold(double fusedScore) {
        if (fusedScore >= blockThreshold) {
            return "block-threshold(" + blockThreshold + ")";
        }
        if (fusedScore >= monitorThreshold) {
            return "monitor-threshold(" + monitorThreshold + ")";
        }
        return "below-monitor-threshold(" + monitorThreshold + ")";
    }
}
