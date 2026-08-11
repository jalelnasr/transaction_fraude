package com.frauddetection.decision.service;

import com.frauddetection.common.enums.DecisionType;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class ScoreFusionTests {

    @Test
    void fusesRuleAndMlScoresWithConfiguredWeights() {
        ScoreFusionService fusionService = new ScoreFusionService();
        ReflectionTestUtils.setField(fusionService, "ruleWeight", 0.4);
        ReflectionTestUtils.setField(fusionService, "mlWeight", 0.6);

        double fused = fusionService.fuse(0.5, 0.8);

        assertThat(fused).isCloseTo(0.68, within(0.0001));
    }

    @Test
    void fusedScoreNeverExceedsOne() {
        ScoreFusionService fusionService = new ScoreFusionService();
        ReflectionTestUtils.setField(fusionService, "ruleWeight", 0.6);
        ReflectionTestUtils.setField(fusionService, "mlWeight", 0.6);

        double fused = fusionService.fuse(1.0, 1.0);

        assertThat(fused).isEqualTo(1.0);
    }

    @Test
    void thresholdServiceClassifiesAccepted() {
        ThresholdService thresholdService = new ThresholdService();
        ReflectionTestUtils.setField(thresholdService, "monitorThreshold", 0.5);
        ReflectionTestUtils.setField(thresholdService, "blockThreshold", 0.85);

        assertThat(thresholdService.decide(0.2)).isEqualTo(DecisionType.ACCEPTED);
    }

    @Test
    void thresholdServiceClassifiesMonitored() {
        ThresholdService thresholdService = new ThresholdService();
        ReflectionTestUtils.setField(thresholdService, "monitorThreshold", 0.5);
        ReflectionTestUtils.setField(thresholdService, "blockThreshold", 0.85);

        assertThat(thresholdService.decide(0.6)).isEqualTo(DecisionType.MONITORED);
    }

    @Test
    void thresholdServiceClassifiesBlocked() {
        ThresholdService thresholdService = new ThresholdService();
        ReflectionTestUtils.setField(thresholdService, "monitorThreshold", 0.5);
        ReflectionTestUtils.setField(thresholdService, "blockThreshold", 0.85);

        assertThat(thresholdService.decide(0.9)).isEqualTo(DecisionType.BLOCKED);
    }
}
