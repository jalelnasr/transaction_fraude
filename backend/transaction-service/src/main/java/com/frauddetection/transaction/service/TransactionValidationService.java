package com.frauddetection.transaction.service;

import com.frauddetection.common.utils.ValidationUtils;
import com.frauddetection.transaction.dto.CreateTransactionRequest;
import org.springframework.stereotype.Service;

@Service
public class TransactionValidationService {

    public void validate(CreateTransactionRequest request) {
        ValidationUtils.requirePositive(request.getAmount(), "amount");
        ValidationUtils.requireNonBlank(request.getCurrency(), "currency");
        ValidationUtils.requireNonBlank(request.getSourceAccountId(), "sourceAccountId");
        ValidationUtils.requireNonBlank(request.getDestinationAccountId(), "destinationAccountId");
        ValidationUtils.requireNonBlank(request.getChannel(), "channel");
    }
}
