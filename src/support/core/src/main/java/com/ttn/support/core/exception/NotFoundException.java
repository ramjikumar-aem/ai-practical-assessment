package com.ttn.support.core.exception;

public class NotFoundException extends SupportApiException {

    public NotFoundException(String message) {
        super("NOT_FOUND", message, 404);
    }
}
