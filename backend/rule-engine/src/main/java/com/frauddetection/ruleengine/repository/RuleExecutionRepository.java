package com.frauddetection.ruleengine.repository;

import com.frauddetection.ruleengine.entity.RuleExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RuleExecutionRepository extends JpaRepository<RuleExecution, String> {

    long countBySourceAccountIdAndEvaluatedAtAfter(String sourceAccountId, Instant after);

    boolean existsBySourceAccountIdAndDestinationAccountId(String sourceAccountId, String destinationAccountId);

    long countBySourceAccountIdAndDestinationAccountIdAndEvaluatedAtAfter(
            String sourceAccountId, String destinationAccountId, Instant after);

    Optional<RuleExecution> findTopBySourceAccountIdOrderByEvaluatedAtDesc(String sourceAccountId);

    @Query("select avg(r.amount) from RuleExecution r where r.sourceAccountId = :sourceAccountId and r.amount is not null")
    Double avgAmountBySourceAccountId(@Param("sourceAccountId") String sourceAccountId);
}
