package com.frauddetection.ruleengine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frauddetection.ruleengine.dto.RuleDTO;
import com.frauddetection.ruleengine.repository.RuleRepository;
import com.frauddetection.ruleengine.service.RuleManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultRuleSeeder implements CommandLineRunner {

    private final RuleRepository ruleRepository;
    private final RuleManagementService ruleManagementService;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        if (ruleRepository.count() > 0) {
            return;
        }

        try (var input = new ClassPathResource("rules/default-rules.json").getInputStream()) {
            List<RuleDTO> defaultRules = objectMapper.readValue(input, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, RuleDTO.class));
            defaultRules.forEach(rule -> ruleManagementService.create(rule, "system"));
            log.info("Seeded {} default rules", defaultRules.size());
        }
    }
}
