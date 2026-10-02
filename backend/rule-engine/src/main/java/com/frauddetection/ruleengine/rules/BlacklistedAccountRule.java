package com.frauddetection.ruleengine.rules;

import com.frauddetection.common.dto.TransactionDTO;
import com.frauddetection.ruleengine.entity.Rule;
import com.frauddetection.ruleengine.enums.RuleType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class BlacklistedAccountRule implements RuleStrategy {

    @Override
    public RuleType supports() {
        return RuleType.BLACKLISTED_ACCOUNT;
    }

    @Override
    public Optional<String> evaluate(TransactionDTO transaction, Rule rule) {
        String blacklist = rule.getBlacklistedAccounts();
        if (blacklist == null || blacklist.isBlank() || transaction.getDestinationAccountId() == null) {
            return Optional.empty();
        }

        boolean isBlacklisted = Arrays.stream(blacklist.split(","))
                .map(String::trim)
                .anyMatch(account -> account.equalsIgnoreCase(transaction.getDestinationAccountId()));

        if (isBlacklisted) {
            return Optional.of("Compte destinataire sur liste noire : " + transaction.getDestinationAccountId());
        }
        return Optional.empty();
    }
}
