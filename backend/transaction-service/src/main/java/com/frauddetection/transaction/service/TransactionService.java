package com.frauddetection.transaction.service;

import com.frauddetection.common.dto.TransactionDTO;
import com.frauddetection.common.enums.DecisionType;
import com.frauddetection.common.enums.TransactionStatus;
import com.frauddetection.common.exceptions.ServiceException;
import com.frauddetection.transaction.dto.CreateTransactionRequest;
import com.frauddetection.transaction.dto.TransactionResponse;
import com.frauddetection.transaction.entity.Transaction;
import com.frauddetection.transaction.kafka.TransactionProducer;
import com.frauddetection.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionValidationService validationService;
    private final TransactionProducer transactionProducer;

    @Transactional
    public TransactionResponse createTransaction(CreateTransactionRequest request) {
        validationService.validate(request);

        String transactionId = UUID.randomUUID().toString();
        Instant timestamp = request.getTimestamp() != null ? request.getTimestamp() : Instant.now();

        Transaction transaction = Transaction.builder()
                .id(transactionId)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .sourceAccountId(request.getSourceAccountId())
                .destinationAccountId(request.getDestinationAccountId())
                .channel(request.getChannel())
                .country(request.getCountry())
                .city(request.getCity())
                .customerId(request.getCustomerId())
                .sourceBalanceBefore(request.getSourceBalanceBefore())
                .sourceBalanceAfter(request.getSourceBalanceAfter())
                .destinationBalanceBefore(request.getDestinationBalanceBefore())
                .destinationBalanceAfter(request.getDestinationBalanceAfter())
                .timestamp(timestamp)
                .status(TransactionStatus.RECEIVED)
                .createdAt(Instant.now())
                .build();

        transactionRepository.save(transaction);

        TransactionDTO transactionDTO = TransactionDTO.builder()
                .transactionId(transactionId)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .sourceAccountId(request.getSourceAccountId())
                .destinationAccountId(request.getDestinationAccountId())
                .channel(request.getChannel())
                .country(request.getCountry())
                .city(request.getCity())
                .customerId(request.getCustomerId())
                .sourceBalanceBefore(request.getSourceBalanceBefore())
                .sourceBalanceAfter(request.getSourceBalanceAfter())
                .destinationBalanceBefore(request.getDestinationBalanceBefore())
                .destinationBalanceAfter(request.getDestinationBalanceAfter())
                .timestamp(timestamp)
                .build();

        transactionProducer.publish(transactionDTO);

        return toResponse(transaction);
    }

    public TransactionResponse getById(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new com.frauddetection.common.exceptions.ServiceException(
                        "Transaction not found: " + transactionId));

        return toResponse(transaction);
    }

    public Page<TransactionResponse> list(Pageable pageable) {
        return transactionRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public void applyDecisionStatus(String transactionId, DecisionType decisionStatus) {
        Transaction transaction = transactionRepository.findById(transactionId).orElse(null);
        if (transaction == null) {
            return;
        }
        transaction.setStatus(TransactionStatus.valueOf(decisionStatus.name()));
        transactionRepository.save(transaction);
    }

    @Transactional
    public TransactionResponse overrideStatus(String transactionId, TransactionStatus newStatus) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ServiceException("Transaction not found: " + transactionId));

        transaction.setStatus(newStatus);
        transactionRepository.save(transaction);
        return toResponse(transaction);
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .sourceAccountId(transaction.getSourceAccountId())
                .destinationAccountId(transaction.getDestinationAccountId())
                .channel(transaction.getChannel())
                .country(transaction.getCountry())
                .status(transaction.getStatus())
                .timestamp(transaction.getTimestamp())
                .build();
    }
}
