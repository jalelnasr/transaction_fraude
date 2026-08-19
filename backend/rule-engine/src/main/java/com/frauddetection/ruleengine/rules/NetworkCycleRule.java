package com.frauddetection.ruleengine.rules;

import com.frauddetection.common.dto.TransactionDTO;
import com.frauddetection.ruleengine.client.GraphEngineClient;
import com.frauddetection.ruleengine.entity.Rule;
import com.frauddetection.ruleengine.enums.RuleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NetworkCycleRule implements RuleStrategy {

    private final GraphEngineClient graphEngineClient;

    @Override
    public RuleType supports() {
        return RuleType.NETWORK_CYCLE;
    }

    @Override
    public Optional<String> evaluate(TransactionDTO transaction, Rule rule) {
        if (transaction.getSourceAccountId() == null) {
            return Optional.empty();
        }

        int cycleCount = graphEngineClient.countCycles(transaction.getSourceAccountId());
        if (cycleCount > 0) {
            return Optional.of("Compte source " + transaction.getSourceAccountId()
                    + " implique dans " + cycleCount + " cycle(s) de transferts (schema de blanchiment potentiel)");
        }
        return Optional.empty();
    }
}
