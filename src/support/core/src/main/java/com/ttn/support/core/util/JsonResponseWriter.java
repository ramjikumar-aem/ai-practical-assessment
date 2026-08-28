package com.ttn.support.core.util;

import com.ttn.support.core.exception.SupportApiException;
import com.ttn.support.core.exception.ValidationException;
import com.ttn.support.core.models.Comment;
import com.ttn.support.core.models.Ticket;
import com.ttn.support.core.models.TicketPriority;
import com.ttn.support.core.models.TicketStatus;
import com.ttn.support.core.models.UserRef;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import java.io.StringReader;
import java.util.Map;

public final class JsonResponseWriter {

    private JsonResponseWriter() {
    }

    public static JsonObject readObject(String body) throws ValidationException {
        if (body == null || body.isBlank()) {
            return Json.createObjectBuilder().build();
        }
        try (JsonReader reader = Json.createReader(new StringReader(body))) {
            return reader.readObject();
        } catch (JsonException ex) {
            throw new ValidationException(
                    "Malformed JSON request body. Use double-quoted JSON (on Windows CMD escape quotes in -d).");
        }
    }

    public static JsonObject toTicketJson(Ticket ticket) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("id", ticket.getId());
        builder.add("title", ticket.getTitle());
        builder.add("description", ticket.getDescription());
        builder.add("priority", ticket.getPriority().name());
        builder.add("status", ticket.getStatus().name());
        builder.add("assignedTo", ticket.getAssignedTo());
        builder.add("createdBy", ticket.getCreatedBy());
        builder.add("createdAt", DateTimeUtil.format(ticket.getCreatedAt()));
        builder.add("updatedAt", DateTimeUtil.format(ticket.getUpdatedAt()));
        return builder.build();
    }

    public static JsonObject toCommentJson(Comment comment) {
        return Json.createObjectBuilder()
                .add("id", comment.getId())
                .add("ticketId", comment.getTicketId())
                .add("message", comment.getMessage())
                .add("createdBy", comment.getCreatedBy())
                .add("createdAt", DateTimeUtil.format(comment.getCreatedAt()))
                .build();
    }

    public static JsonObject toUserJson(UserRef user) {
        return Json.createObjectBuilder()
                .add("id", user.getId())
                .add("name", user.getName() == null ? "" : user.getName())
                .add("email", user.getEmail() == null ? "" : user.getEmail())
                .add("role", user.getRole() == null ? "" : user.getRole())
                .build();
    }

    public static JsonObject error(SupportApiException exception) {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("code", exception.getCode())
                .add("message", exception.getMessage());
        if (!exception.getFields().isEmpty()) {
            JsonObjectBuilder fields = Json.createObjectBuilder();
            for (Map.Entry<String, String> entry : exception.getFields().entrySet()) {
                fields.add(entry.getKey(), entry.getValue());
            }
            builder.add("fields", fields);
        }
        return builder.build();
    }

    public static JsonObject error(String code, String message) {
        return Json.createObjectBuilder()
                .add("code", code)
                .add("message", message)
                .build();
    }

    public static JsonObject ticketsList(Iterable<Ticket> tickets) {
        JsonArrayBuilder items = Json.createArrayBuilder();
        for (Ticket ticket : tickets) {
            items.add(toTicketJson(ticket));
        }
        return Json.createObjectBuilder().add("items", items).build();
    }

    public static JsonObject commentsList(Iterable<Comment> comments) {
        JsonArrayBuilder items = Json.createArrayBuilder();
        for (Comment comment : comments) {
            items.add(toCommentJson(comment));
        }
        return Json.createObjectBuilder().add("items", items).build();
    }

    public static JsonObject usersList(Iterable<UserRef> users) {
        JsonArrayBuilder items = Json.createArrayBuilder();
        for (UserRef user : users) {
            items.add(toUserJson(user));
        }
        return Json.createObjectBuilder().add("items", items).build();
    }
}
