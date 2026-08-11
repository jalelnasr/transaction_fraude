package com.frauddetection.decision.repository;

import com.frauddetection.decision.entity.FraudDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DecisionRepository extends JpaRepository<FraudDecision, String> {

    Optional<FraudDecision> findByTransactionId(String transactionId);
}
