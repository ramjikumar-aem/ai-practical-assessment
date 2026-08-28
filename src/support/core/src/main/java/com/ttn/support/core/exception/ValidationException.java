package com.ttn.support.core.exception;

import java.util.Map;

public class ValidationException extends SupportApiException {

    public ValidationException(String message) {
        this(message, Map.of());
    }

    public ValidationException(String message, Map<String, String> fields) {
        super("VALIDATION_ERROR", message, 400, fields);
    }
}
