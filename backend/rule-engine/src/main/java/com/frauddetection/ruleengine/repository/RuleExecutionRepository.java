package com.frauddetection.ruleengine.repository;

import com.frauddetection.ruleengine.entity.RuleExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface RuleExecutionRepository extends JpaRepository<RuleExecution, String> {

    long countBySourceAccountIdAndEvaluatedAtAfter(String sourceAccountId, Instant after);
}
