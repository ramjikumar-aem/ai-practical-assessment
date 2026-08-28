package com.ttn.support.core.service;

import com.ttn.support.core.exception.InvalidTransitionException;
import com.ttn.support.core.models.TicketStatus;
import org.osgi.service.component.annotations.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component(service = StatusTransitionService.class)
public class StatusTransitionService {

    private final Map<TicketStatus, Set<TicketStatus>> transitions = buildTransitions();

    public void validateTransition(TicketStatus current, TicketStatus target) throws InvalidTransitionException {
        if (current == null || target == null) {
            throw new InvalidTransitionException("Status values are required");
        }
        if (current == target) {
            throw new InvalidTransitionException(current.name() + " cannot transition to " + target.name());
        }
        Set<TicketStatus> allowed = transitions.getOrDefault(current, Collections.emptySet());
        if (!allowed.contains(target)) {
            throw new InvalidTransitionException(current.name() + " cannot transition to " + target.name());
        }
    }

    public Set<TicketStatus> getAllowedTargets(TicketStatus current) {
        if (current == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(transitions.getOrDefault(current, Collections.emptySet()));
    }

    public boolean isTerminal(TicketStatus status) {
        return status == TicketStatus.CLOSED || status == TicketStatus.CANCELLED;
    }

    private static Map<TicketStatus, Set<TicketStatus>> buildTransitions() {
        Map<TicketStatus, Set<TicketStatus>> map = new EnumMap<>(TicketStatus.class);
        map.put(TicketStatus.OPEN, EnumSet.of(TicketStatus.IN_PROGRESS, TicketStatus.CANCELLED));
        map.put(TicketStatus.IN_PROGRESS, EnumSet.of(TicketStatus.RESOLVED, TicketStatus.CANCELLED));
        map.put(TicketStatus.RESOLVED, EnumSet.of(TicketStatus.CLOSED));
        map.put(TicketStatus.CLOSED, EnumSet.noneOf(TicketStatus.class));
        map.put(TicketStatus.CANCELLED, EnumSet.noneOf(TicketStatus.class));
        return map;
    }
}
