package com.frauddetection.explainability.controller;

import com.frauddetection.common.exceptions.ServiceException;
import com.frauddetection.explainability.entity.Explanation;
import com.frauddetection.explainability.repository.ExplanationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class ExplanationController {

    private final ExplanationRepository explanationRepository;

    @GetMapping("/{id}/explanation")
    public ResponseEntity<Explanation> getExplanation(@PathVariable("id") String transactionId) {
        return explanationRepository.findByTransactionId(transactionId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ServiceException("No explanation found for transaction: " + transactionId));
    }
}
