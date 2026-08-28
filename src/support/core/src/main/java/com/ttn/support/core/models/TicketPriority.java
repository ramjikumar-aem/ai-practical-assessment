package com.ttn.support.core.models;

import java.util.Arrays;
import java.util.Optional;

public enum TicketPriority {
    LOW,
    MEDIUM,
    HIGH;

    public static Optional<TicketPriority> fromString(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(priority -> priority.name().equalsIgnoreCase(value.trim()))
                .findFirst();
    }
}
