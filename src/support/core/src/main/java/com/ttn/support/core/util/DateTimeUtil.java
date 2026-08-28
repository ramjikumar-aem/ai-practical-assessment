package com.ttn.support.core.util;

import java.time.Instant;
import java.time.format.DateTimeParseException;

public final class DateTimeUtil {

    private DateTimeUtil() {
    }

    public static String format(Instant instant) {
        return instant == null ? null : instant.toString();
    }

    public static Instant parse(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Instant.parse(value);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
