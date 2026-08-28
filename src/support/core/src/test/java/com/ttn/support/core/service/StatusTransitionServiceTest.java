package com.ttn.support.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ttn.support.core.exception.InvalidTransitionException;
import com.ttn.support.core.models.TicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StatusTransitionServiceTest {

    private StatusTransitionService service;

    @BeforeEach
    void setUp() {
        service = new StatusTransitionService();
    }

    @Test
    void allowsValidTransitions() throws InvalidTransitionException {
        service.validateTransition(TicketStatus.OPEN, TicketStatus.IN_PROGRESS);
        service.validateTransition(TicketStatus.OPEN, TicketStatus.CANCELLED);
        service.validateTransition(TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED);
        service.validateTransition(TicketStatus.IN_PROGRESS, TicketStatus.CANCELLED);
        service.validateTransition(TicketStatus.RESOLVED, TicketStatus.CLOSED);
    }

    @Test
    void rejectsInvalidTransitions() {
        assertInvalid(TicketStatus.OPEN, TicketStatus.RESOLVED);
        assertInvalid(TicketStatus.OPEN, TicketStatus.CLOSED);
        assertInvalid(TicketStatus.IN_PROGRESS, TicketStatus.OPEN);
        assertInvalid(TicketStatus.RESOLVED, TicketStatus.IN_PROGRESS);
        assertInvalid(TicketStatus.CLOSED, TicketStatus.OPEN);
        assertInvalid(TicketStatus.CANCELLED, TicketStatus.OPEN);
    }

    @Test
    void terminalStatesHaveNoOutgoingTransitions() {
        assertTrue(service.getAllowedTargets(TicketStatus.CLOSED).isEmpty());
        assertTrue(service.getAllowedTargets(TicketStatus.CANCELLED).isEmpty());
    }

    private void assertInvalid(TicketStatus current, TicketStatus target) {
        InvalidTransitionException exception = assertThrows(
                InvalidTransitionException.class,
                () -> service.validateTransition(current, target));
        assertEquals("INVALID_STATUS_TRANSITION", exception.getCode());
    }
}
