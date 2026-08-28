package com.ttn.support.core.repository;

import com.ttn.support.core.constants.SupportConstants;
import com.ttn.support.core.models.Comment;
import com.ttn.support.core.models.Ticket;
import com.ttn.support.core.models.TicketPriority;
import com.ttn.support.core.models.TicketStatus;
import com.ttn.support.core.util.DateTimeUtil;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.osgi.service.component.annotations.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component(service = TicketRepository.class)
public class TicketRepository {

    public Ticket create(ResourceResolver resolver, Ticket ticket) throws PersistenceException {
        ensureTicketsRoot(resolver);
        String id = ticket.getId() != null ? ticket.getId() : UUID.randomUUID().toString();
        Resource resource = resolver.getResource(SupportConstants.TICKETS_PATH);
        Resource ticketResource = resolver.create(resource, id, null);
        ticket.setId(id);
        writeTicket(ticketResource, ticket);
        resolver.commit();
        return ticket;
    }

    public Optional<Ticket> findById(ResourceResolver resolver, String id) {
        Resource resource = resolver.getResource(SupportConstants.TICKETS_PATH + "/" + id);
        if (resource == null || ResourceUtil.isNonExistingResource(resource)) {
            return Optional.empty();
        }
        return Optional.of(readTicket(resource));
    }

    public List<Ticket> findAll(ResourceResolver resolver, String keyword, TicketStatus statusFilter) {
        Resource root = resolver.getResource(SupportConstants.TICKETS_PATH);
        if (root == null) {
            return List.of();
        }
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        return StreamSupport.stream(root.getChildren().spliterator(), false)
                .map(this::readTicket)
                .filter(ticket -> statusFilter == null || ticket.getStatus() == statusFilter)
                .filter(ticket -> matchesKeyword(ticket, normalizedKeyword))
                .sorted(Comparator.comparing(Ticket::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Ticket save(ResourceResolver resolver, Ticket ticket) throws PersistenceException {
        Resource resource = resolver.getResource(SupportConstants.TICKETS_PATH + "/" + ticket.getId());
        if (resource == null) {
            throw new PersistenceException("Ticket not found: " + ticket.getId());
        }
        writeTicket(resource, ticket);
        resolver.commit();
        return ticket;
    }

    private void ensureTicketsRoot(ResourceResolver resolver) throws PersistenceException {
        if (resolver.getResource(SupportConstants.ROOT_PATH) == null) {
            resolver.create(resolver.getResource("/var"), "support-tickets", null);
        }
        if (resolver.getResource(SupportConstants.TICKETS_PATH) == null) {
            resolver.create(resolver.getResource(SupportConstants.ROOT_PATH), "tickets", null);
        }
    }

    private boolean matchesKeyword(Ticket ticket, String keyword) {
        if (keyword.isEmpty()) {
            return true;
        }
        return contains(ticket.getTitle(), keyword) || contains(ticket.getDescription(), keyword);
    }

    private boolean contains(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private Ticket readTicket(Resource resource) {
        Ticket ticket = new Ticket();
        ticket.setId(resource.getName());
        ticket.setTitle(resource.getValueMap().get("title", String.class));
        ticket.setDescription(resource.getValueMap().get("description", String.class));
        ticket.setPriority(TicketPriority.valueOf(resource.getValueMap().get("priority", "MEDIUM")));
        ticket.setStatus(TicketStatus.valueOf(resource.getValueMap().get("status", TicketStatus.OPEN.name())));
        ticket.setAssignedTo(resource.getValueMap().get("assignedTo", String.class));
        ticket.setCreatedBy(resource.getValueMap().get("createdBy", String.class));
        ticket.setCreatedAt(DateTimeUtil.parse(resource.getValueMap().get("createdAt", String.class)));
        ticket.setUpdatedAt(DateTimeUtil.parse(resource.getValueMap().get("updatedAt", String.class)));
        return ticket;
    }

    private void writeTicket(Resource resource, Ticket ticket) throws PersistenceException {
        ModifiableValueMap properties = resource.adaptTo(ModifiableValueMap.class);
        if (properties == null) {
            throw new PersistenceException("Unable to adapt ticket resource");
        }
        properties.put("title", ticket.getTitle());
        properties.put("description", ticket.getDescription());
        properties.put("priority", ticket.getPriority().name());
        properties.put("status", ticket.getStatus().name());
        properties.put("assignedTo", ticket.getAssignedTo());
        properties.put("createdBy", ticket.getCreatedBy());
        properties.put("createdAt", DateTimeUtil.format(ticket.getCreatedAt()));
        properties.put("updatedAt", DateTimeUtil.format(ticket.getUpdatedAt()));
    }
}
