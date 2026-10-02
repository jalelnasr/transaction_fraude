package com.frauddetection.ruleengine.rules;

import com.frauddetection.common.dto.TransactionDTO;
import com.frauddetection.ruleengine.entity.Rule;
import com.frauddetection.ruleengine.entity.RuleExecution;
import com.frauddetection.ruleengine.enums.RuleType;
import com.frauddetection.ruleengine.repository.RuleExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Approxime un changement d'appareil/localisation via le changement de pays
 * par rapport a la derniere transaction connue du meme compte source.
 * Aucune empreinte d'appareil (device fingerprint) n'existe dans le modele de donnees actuel.
 */
@Component
@RequiredArgsConstructor
public class LocationChangeRule implements RuleStrategy {

    private final RuleExecutionRepository ruleExecutionRepository;

    @Override
    public RuleType supports() {
        return RuleType.LOCATION_CHANGE;
    }

    @Override
    public Optional<String> evaluate(TransactionDTO transaction, Rule rule) {
        if (transaction.getSourceAccountId() == null || transaction.getCountry() == null) {
            return Optional.empty();
        }

        Optional<RuleExecution> lastExecution = ruleExecutionRepository
                .findTopBySourceAccountIdOrderByEvaluatedAtDesc(transaction.getSourceAccountId());

        if (lastExecution.isEmpty() || lastExecution.get().getCountry() == null) {
            return Optional.empty();
        }

        String previousCountry = lastExecution.get().getCountry();
        if (!previousCountry.equalsIgnoreCase(transaction.getCountry())) {
            return Optional.of("Changement de pays detecte : " + previousCountry + " -> " + transaction.getCountry());
        }
        return Optional.empty();
    }
}
