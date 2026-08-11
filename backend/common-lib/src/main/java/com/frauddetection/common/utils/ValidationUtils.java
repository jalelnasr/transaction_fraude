package com.frauddetection.common.utils;

import com.frauddetection.common.exceptions.ValidationException;

import java.math.BigDecimal;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " is required");
        }
    }

    public static void requirePositive(BigDecimal value, String fieldName) {
        if (value == null || value.signum() <= 0) {
            throw new ValidationException(fieldName + " must be a positive amount");
        }
    }
}
