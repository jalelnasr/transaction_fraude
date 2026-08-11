package com.frauddetection.ruleengine.rules;

import com.frauddetection.common.dto.TransactionDTO;
import com.frauddetection.ruleengine.entity.Rule;
import com.frauddetection.ruleengine.enums.RuleType;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Optional;

@Component
public class TimePatternRule implements RuleStrategy {

    @Override
    public RuleType supports() {
        return RuleType.TIME_PATTERN;
    }

    @Override
    public Optional<String> evaluate(TransactionDTO transaction, Rule rule) {
        Integer nightStart = rule.getNightStartHour();
        Integer nightEnd = rule.getNightEndHour();
        if (nightStart == null || nightEnd == null || transaction.getTimestamp() == null) {
            return Optional.empty();
        }

        int hour = transaction.getTimestamp().atZone(ZoneOffset.UTC).getHour();
        boolean isNight = nightStart < nightEnd
                ? (hour >= nightStart && hour < nightEnd)
                : (hour >= nightStart || hour < nightEnd);

        if (isNight) {
            return Optional.of("Transaction effectuee en heure creuse a risque (" + hour + "h UTC)");
        }
        return Optional.empty();
    }
}
