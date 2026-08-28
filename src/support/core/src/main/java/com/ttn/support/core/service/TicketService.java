package com.ttn.support.core.service;

import com.ttn.support.core.exception.NotFoundException;
import com.ttn.support.core.exception.SupportApiException;
import com.ttn.support.core.exception.ValidationException;
import com.ttn.support.core.models.Ticket;
import com.ttn.support.core.models.TicketPriority;
import com.ttn.support.core.models.TicketStatus;
import com.ttn.support.core.repository.TicketRepository;
import com.ttn.support.core.validation.TicketValidator;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.json.JsonObject;
import javax.jcr.RepositoryException;
import java.time.Instant;
import java.util.List;

@Component(service = TicketService.class)
public class TicketService {

    @Reference
    private TicketRepository ticketRepository;

    @Reference
    private StatusTransitionService statusTransitionService;

    @Reference
    private UserService userService;

    @Reference
    private TicketValidator ticketValidator;

    public Ticket create(ResourceResolver resolver, JsonObject payload, String currentUserId)
            throws SupportApiException, PersistenceException, RepositoryException {
        ticketValidator.validateCreate(payload, currentUserId);
        userService.validateAssignee(resolver, payload.getString("assignedTo"));

        Ticket ticket = new Ticket();
        ticket.setTitle(payload.getString("title"));
        ticket.setDescription(payload.getString("description"));
        ticket.setPriority(ticketValidator.readPriority(payload).orElse(TicketPriority.MEDIUM));
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setAssignedTo(payload.getString("assignedTo"));
        ticket.setCreatedBy(currentUserId);
        Instant now = Instant.now();
        ticket.setCreatedAt(now);
        ticket.setUpdatedAt(now);
        return ticketRepository.create(resolver, ticket);
    }

    public List<Ticket> list(ResourceResolver resolver, String keyword, String statusFilter) {
        TicketStatus status = statusFilter == null || statusFilter.isBlank()
                ? null
                : TicketStatus.fromString(statusFilter).orElse(null);
        return ticketRepository.findAll(resolver, keyword, status);
    }

    public Ticket getById(ResourceResolver resolver, String ticketId) throws NotFoundException {
        return ticketRepository.findById(resolver, ticketId)
                .orElseThrow(() -> new NotFoundException("Ticket not found: " + ticketId));
    }

    public Ticket update(ResourceResolver resolver, String ticketId, JsonObject payload, String currentUserId)
            throws SupportApiException, PersistenceException, RepositoryException {
        ticketValidator.validateUpdate(payload);
        Ticket ticket = getById(resolver, ticketId);
        if (payload.containsKey("title")) {
            ticket.setTitle(payload.getString("title"));
        }
        if (payload.containsKey("description")) {
            ticket.setDescription(payload.getString("description"));
        }
        if (payload.containsKey("priority")) {
            ticket.setPriority(ticketValidator.readPriority(payload).orElse(ticket.getPriority()));
        }
        if (payload.containsKey("assignedTo")) {
            userService.validateAssignee(resolver, payload.getString("assignedTo"));
            ticket.setAssignedTo(payload.getString("assignedTo"));
        }
        ticket.setUpdatedAt(Instant.now());
        return ticketRepository.save(resolver, ticket);
    }

    public Ticket transitionStatus(ResourceResolver resolver, String ticketId, JsonObject payload)
            throws SupportApiException, PersistenceException {
        ticketValidator.validateStatusTransition(payload);
        Ticket ticket = getById(resolver, ticketId);
        TicketStatus target = ticketValidator.readStatus(payload).orElseThrow(
                () -> new ValidationException("Status transition validation failed"));
        statusTransitionService.validateTransition(ticket.getStatus(), target);
        ticket.setStatus(target);
        ticket.setUpdatedAt(Instant.now());
        return ticketRepository.save(resolver, ticket);
    }
}
