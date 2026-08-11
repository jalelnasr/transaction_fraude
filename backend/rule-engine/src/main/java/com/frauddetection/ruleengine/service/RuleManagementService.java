package com.frauddetection.ruleengine.service;

import com.frauddetection.common.audit.AuditLogger;
import com.frauddetection.common.enums.AuditEventType;
import com.frauddetection.common.exceptions.ServiceException;
import com.frauddetection.ruleengine.dto.RuleDTO;
import com.frauddetection.ruleengine.entity.Rule;
import com.frauddetection.ruleengine.repository.RuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RuleManagementService {

    private final RuleRepository ruleRepository;
    private final AuditLogger auditLogger;

    public List<Rule> listAll() {
        return ruleRepository.findAll();
    }

    @Transactional
    public Rule create(RuleDTO dto, String username) {
        Rule rule = Rule.builder()
                .id(UUID.randomUUID().toString())
                .name(dto.getName())
                .type(dto.getType())
                .active(dto.isActive())
                .weight(dto.getWeight())
                .thresholdAmount(dto.getThresholdAmount())
                .riskCountries(dto.getRiskCountries())
                .maxTransactionsPerWindow(dto.getMaxTransactionsPerWindow())
                .windowMinutes(dto.getWindowMinutes())
                .nightStartHour(dto.getNightStartHour())
                .nightEndHour(dto.getNightEndHour())
                .createdAt(Instant.now())
                .build();

        ruleRepository.save(rule);
        auditLogger.log(AuditEventType.RULE_CREATED, username, rule.getId(), "Rule created: " + rule.getName());
        return rule;
    }

    @Transactional
    public Rule update(String ruleId, RuleDTO dto, String username) {
        Rule rule = ruleRepository.findById(ruleId)
                .orElseThrow(() -> new ServiceException("Rule not found: " + ruleId));

        rule.setName(dto.getName());
        rule.setType(dto.getType());
        rule.setActive(dto.isActive());
        rule.setWeight(dto.getWeight());
        rule.setThresholdAmount(dto.getThresholdAmount());
        rule.setRiskCountries(dto.getRiskCountries());
        rule.setMaxTransactionsPerWindow(dto.getMaxTransactionsPerWindow());
        rule.setWindowMinutes(dto.getWindowMinutes());
        rule.setNightStartHour(dto.getNightStartHour());
        rule.setNightEndHour(dto.getNightEndHour());
        rule.setUpdatedAt(Instant.now());

        ruleRepository.save(rule);
        auditLogger.log(AuditEventType.RULE_UPDATED, username, rule.getId(), "Rule updated: " + rule.getName());
        return rule;
    }
}
