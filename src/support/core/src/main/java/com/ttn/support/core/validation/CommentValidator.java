package com.ttn.support.core.validation;

import com.ttn.support.core.exception.ValidationException;

import javax.json.JsonObject;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

@Component(service = CommentValidator.class)
public class CommentValidator {

    public void validateCreate(JsonObject payload, String currentUserId) throws ValidationException {
        Map<String, String> fields = new HashMap<>();
        if (!payload.containsKey("message") || payload.getString("message", "").isBlank()) {
            fields.put("message", "required");
        }
        if (currentUserId == null || currentUserId.isBlank()) {
            fields.put("createdBy", "required");
        }
        if (!fields.isEmpty()) {
            throw new ValidationException("Comment validation failed", fields);
        }
    }
}
