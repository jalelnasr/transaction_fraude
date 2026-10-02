package com.frauddetection.ruleengine.rules;

import com.frauddetection.common.dto.TransactionDTO;
import com.frauddetection.ruleengine.entity.Rule;
import com.frauddetection.ruleengine.enums.RuleType;
import com.frauddetection.ruleengine.repository.RuleExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NewBeneficiaryRule implements RuleStrategy {

    private final RuleExecutionRepository ruleExecutionRepository;

    @Override
    public RuleType supports() {
        return RuleType.NEW_BENEFICIARY;
    }

    @Override
    public Optional<String> evaluate(TransactionDTO transaction, Rule rule) {
        if (transaction.getSourceAccountId() == null || transaction.getDestinationAccountId() == null) {
            return Optional.empty();
        }

        boolean alreadyUsed = ruleExecutionRepository.existsBySourceAccountIdAndDestinationAccountId(
                transaction.getSourceAccountId(), transaction.getDestinationAccountId());

        if (!alreadyUsed) {
            return Optional.of("Nouveau beneficiaire jamais utilise par ce client : "
                    + transaction.getDestinationAccountId());
        }
        return Optional.empty();
    }
}
