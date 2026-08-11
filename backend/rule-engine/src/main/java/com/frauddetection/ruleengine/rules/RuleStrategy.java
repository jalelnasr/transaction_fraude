package com.frauddetection.ruleengine.rules;

import com.frauddetection.common.dto.TransactionDTO;
import com.frauddetection.ruleengine.entity.Rule;
import com.frauddetection.ruleengine.enums.RuleType;

import java.util.Optional;

public interface RuleStrategy {

    RuleType supports();

    Optional<String> evaluate(TransactionDTO transaction, Rule rule);
}
