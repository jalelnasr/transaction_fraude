package com.frauddetection.explainability.repository;

import com.frauddetection.explainability.entity.Explanation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExplanationRepository extends JpaRepository<Explanation, String> {

    Optional<Explanation> findByTransactionId(String transactionId);
}
