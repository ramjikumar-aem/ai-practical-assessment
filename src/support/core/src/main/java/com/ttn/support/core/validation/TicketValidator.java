package com.ttn.support.core.validation;

import com.ttn.support.core.exception.ValidationException;
import com.ttn.support.core.models.TicketPriority;
import com.ttn.support.core.models.TicketStatus;

import javax.json.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.osgi.service.component.annotations.Component;

@Component(service = TicketValidator.class)
public class TicketValidator {

    public void validateCreate(JsonObject payload, String currentUserId) throws ValidationException {
        Map<String, String> fields = new HashMap<>();
        requireText(payload, "title", fields);
        requireText(payload, "description", fields);
        requirePriority(payload, fields);
        requireText(payload, "assignedTo", fields);
        if (currentUserId == null || currentUserId.isBlank()) {
            fields.put("createdBy", "required");
        }
        if (!fields.isEmpty()) {
            throw new ValidationException("Ticket validation failed", fields);
        }
    }

    public void validateUpdate(JsonObject payload) throws ValidationException {
        if (payload == null || payload.isEmpty()) {
            throw new ValidationException("At least one field is required for update");
        }
        Map<String, String> fields = new HashMap<>();
        if (payload.containsKey("title")) {
            requireText(payload, "title", fields);
        }
        if (payload.containsKey("description")) {
            requireText(payload, "description", fields);
        }
        if (payload.containsKey("priority")) {
            requirePriority(payload, fields);
        }
        if (payload.containsKey("assignedTo")) {
            requireText(payload, "assignedTo", fields);
        }
        if (!fields.isEmpty()) {
            throw new ValidationException("Ticket update validation failed", fields);
        }
    }

    public void validateStatusTransition(JsonObject payload) throws ValidationException {
        Map<String, String> fields = new HashMap<>();
        if (!payload.containsKey("status") || payload.getString("status", "").isBlank()) {
            fields.put("status", "required");
        } else if (TicketStatus.fromString(payload.getString("status")).isEmpty()) {
            fields.put("status", "invalid");
        }
        if (!fields.isEmpty()) {
            throw new ValidationException("Status transition validation failed", fields);
        }
    }

    public Optional<TicketPriority> readPriority(JsonObject payload) {
        return TicketPriority.fromString(payload.getString("priority", null));
    }

    public Optional<TicketStatus> readStatus(JsonObject payload) {
        return TicketStatus.fromString(payload.getString("status", null));
    }

    private void requireText(JsonObject payload, String field, Map<String, String> fields) {
        if (!payload.containsKey(field) || payload.getString(field, "").isBlank()) {
            fields.put(field, "required");
        }
    }

    private void requirePriority(JsonObject payload, Map<String, String> fields) {
        if (!payload.containsKey("priority") || payload.getString("priority", "").isBlank()) {
            fields.put("priority", "required");
        } else if (TicketPriority.fromString(payload.getString("priority")).isEmpty()) {
            fields.put("priority", "invalid");
        }
    }
}
