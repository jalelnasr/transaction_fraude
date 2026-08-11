package com.frauddetection.ruleengine.rules;

import com.frauddetection.common.dto.TransactionDTO;
import com.frauddetection.ruleengine.entity.Rule;
import com.frauddetection.ruleengine.enums.RuleType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
public class GeolocationRule implements RuleStrategy {

    @Override
    public RuleType supports() {
        return RuleType.GEOLOCATION;
    }

    @Override
    public Optional<String> evaluate(TransactionDTO transaction, Rule rule) {
        String riskCountries = rule.getRiskCountries();
        String country = transaction.getCountry();
        if (riskCountries == null || riskCountries.isBlank() || country == null) {
            return Optional.empty();
        }

        boolean isRisky = Arrays.stream(riskCountries.split(","))
                .map(String::trim)
                .anyMatch(c -> c.equalsIgnoreCase(country));

        if (isRisky) {
            return Optional.of("Transaction emise depuis un pays a risque: " + country);
        }
        return Optional.empty();
    }
}
