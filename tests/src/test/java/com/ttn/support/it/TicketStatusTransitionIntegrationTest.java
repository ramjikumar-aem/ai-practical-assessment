package com.ttn.support.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ttn.support.core.constants.SupportConstants;
import com.ttn.support.core.exception.InvalidTransitionException;
import com.ttn.support.core.exception.SupportApiException;
import com.ttn.support.core.models.Ticket;
import com.ttn.support.core.models.TicketPriority;
import com.ttn.support.core.models.TicketStatus;
import com.ttn.support.core.repository.TicketRepository;
import com.ttn.support.core.service.TicketService;
import com.ttn.support.core.testcontext.SupportTestContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import java.time.Instant;
import javax.json.Json;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@ExtendWith(AemContextExtension.class)
class TicketStatusTransitionIntegrationTest {

    private final AemContext context = new AemContextBuilder()
            .afterSetUp(SupportTestContext.SUPPORT_SERVICES)
            .build();

    private TicketService ticketService;
    private TicketRepository ticketRepository;

    @BeforeEach
    void setUp() {
        ticketService = context.getService(TicketService.class);
        ticketRepository = context.getService(TicketRepository.class);
    }

    @ParameterizedTest
    @CsvSource({
            "OPEN,IN_PROGRESS",
            "OPEN,CANCELLED",
            "IN_PROGRESS,RESOLVED",
            "IN_PROGRESS,CANCELLED",
            "RESOLVED,CLOSED"
    })
    void validTransitionsPersistNewStatus(String currentStatus, String targetStatus) throws Exception {
        Ticket seeded = seedTicket(TicketStatus.valueOf(currentStatus));
        ResourceResolver resolver = context.resourceResolver();

        Ticket updated = ticketService.transitionStatus(
                resolver,
                seeded.getId(),
                Json.createObjectBuilder().add("status", targetStatus).build());

        assertEquals(TicketStatus.valueOf(targetStatus), updated.getStatus());
        assertEquals(
                TicketStatus.valueOf(targetStatus),
                ticketRepository.findById(resolver, seeded.getId()).orElseThrow().getStatus());
    }

    @ParameterizedTest
    @CsvSource({
            "OPEN,RESOLVED",
            "OPEN,CLOSED",
            "IN_PROGRESS,OPEN",
            "RESOLVED,IN_PROGRESS",
            "CLOSED,OPEN",
            "CANCELLED,OPEN"
    })
    void invalidTransitionsAreRejectedWithoutMutation(String currentStatus, String targetStatus) throws Exception {
        Ticket seeded = seedTicket(TicketStatus.valueOf(currentStatus));
        ResourceResolver resolver = context.resourceResolver();

        SupportApiException exception = assertThrows(
                SupportApiException.class,
                () -> ticketService.transitionStatus(
                        resolver,
                        seeded.getId(),
                        Json.createObjectBuilder().add("status", targetStatus).build()));

        assertEquals(409, exception.getStatus());
        assertEquals("INVALID_STATUS_TRANSITION", exception.getCode());
        assertEquals(
                TicketStatus.valueOf(currentStatus),
                ticketRepository.findById(resolver, seeded.getId()).orElseThrow().getStatus());
    }

    private Ticket seedTicket(TicketStatus status) throws Exception {
        Ticket ticket = new Ticket();
        ticket.setTitle("Seed ticket");
        ticket.setDescription("Seed description");
        ticket.setPriority(TicketPriority.MEDIUM);
        ticket.setStatus(status);
        ticket.setAssignedTo("admin");
        ticket.setCreatedBy("admin");
        Instant now = Instant.now();
        ticket.setCreatedAt(now);
        ticket.setUpdatedAt(now);
        return ticketRepository.create(context.resourceResolver(), ticket);
    }
}
