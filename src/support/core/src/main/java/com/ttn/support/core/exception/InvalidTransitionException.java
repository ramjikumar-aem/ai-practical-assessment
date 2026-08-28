package com.ttn.support.core.exception;

public class InvalidTransitionException extends SupportApiException {

    public InvalidTransitionException(String message) {
        super("INVALID_STATUS_TRANSITION", message, 409);
    }
}
