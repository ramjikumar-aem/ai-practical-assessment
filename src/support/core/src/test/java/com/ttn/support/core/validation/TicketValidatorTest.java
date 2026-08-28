package com.ttn.support.core.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ttn.support.core.exception.ValidationException;
import javax.json.Json;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TicketValidatorTest {

    private TicketValidator validator;

    @BeforeEach
    void setUp() {
        validator = new TicketValidator();
    }

    @Test
    void acceptsValidCreatePayload() {
        assertDoesNotThrow(() -> validator.validateCreate(
                Json.createObjectBuilder()
                        .add("title", "Login issue")
                        .add("description", "Cannot login")
                        .add("priority", "HIGH")
                        .add("assignedTo", "support-agent")
                        .build(),
                "admin"));
    }

    @Test
    void rejectsMissingRequiredFields() {
        assertThrows(ValidationException.class, () -> validator.validateCreate(
                Json.createObjectBuilder().build(),
                "admin"));
    }
}
