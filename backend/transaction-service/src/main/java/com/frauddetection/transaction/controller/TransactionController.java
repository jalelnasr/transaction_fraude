package com.frauddetection.transaction.controller;

import com.frauddetection.transaction.dto.CreateTransactionRequest;
import com.frauddetection.transaction.dto.TransactionResponse;
import com.frauddetection.transaction.dto.UpdateStatusRequest;
import com.frauddetection.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody CreateTransactionRequest request) {
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getById(@PathVariable("id") String id) {
        return ResponseEntity.ok(transactionService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> list(@PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(transactionService.list(pageable));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TransactionResponse> updateStatus(@PathVariable("id") String id,
                                                              @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(transactionService.overrideStatus(id, request.getStatus()));
    }
}
