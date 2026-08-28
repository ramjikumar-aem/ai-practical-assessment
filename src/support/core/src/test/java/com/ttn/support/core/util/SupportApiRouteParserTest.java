package com.ttn.support.core.util;

import org.apache.sling.api.request.RequestPathInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SupportApiRouteParserTest {

    @Mock
    private RequestPathInfo pathInfo;

    @Test
    void resolveCollectionRoute() {
        when(pathInfo.getExtension()).thenReturn("json");
        when(pathInfo.getSelectors()).thenReturn(new String[0]);

        SupportApiRouteParser.TicketRoute route = SupportApiRouteParser.resolve(pathInfo);

        assertTrue(route.isCollection());
    }

    @Test
    void resolveTicketRoute() {
        when(pathInfo.getExtension()).thenReturn("json");
        when(pathInfo.getSelectors()).thenReturn(new String[] { "967c4e7f-2620-4a67-a80a-c253f3d8aaf2" });

        SupportApiRouteParser.TicketRoute route = SupportApiRouteParser.resolve(pathInfo);

        assertTrue(route.isTicket());
        assertEquals("967c4e7f-2620-4a67-a80a-c253f3d8aaf2", route.ticketId());
    }

    @Test
    void resolveStatusRoute() {
        when(pathInfo.getExtension()).thenReturn("json");
        when(pathInfo.getSelectors()).thenReturn(new String[] { "ticket-id", "status" });

        SupportApiRouteParser.TicketRoute route = SupportApiRouteParser.resolve(pathInfo);

        assertTrue(route.isStatus());
        assertEquals("ticket-id", route.ticketId());
    }

    @Test
    void resolveCommentsRoute() {
        when(pathInfo.getExtension()).thenReturn("json");
        when(pathInfo.getSelectors()).thenReturn(new String[] { "ticket-id", "comments" });

        SupportApiRouteParser.TicketRoute route = SupportApiRouteParser.resolve(pathInfo);

        assertTrue(route.isComments());
        assertEquals("ticket-id", route.ticketId());
    }

    @Test
    void resolveUnknownWhenExtensionMissing() {
        when(pathInfo.getExtension()).thenReturn(null);

        SupportApiRouteParser.TicketRoute route = SupportApiRouteParser.resolve(pathInfo);

        assertTrue(route.isUnknown());
    }
}
