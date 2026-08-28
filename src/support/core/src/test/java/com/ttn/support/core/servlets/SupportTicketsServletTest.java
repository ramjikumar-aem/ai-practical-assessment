package com.ttn.support.core.servlets;

import com.ttn.support.core.constants.SupportConstants;
import com.ttn.support.core.testcontext.SupportTestContext;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class SupportTicketsServletTest {

    private final AemContext context = new AemContext();
    private SupportTicketsServlet servlet;

    @BeforeEach
    void setUp() throws Exception {
        SupportTestContext.SUPPORT_SERVICES.execute(context);
        servlet = context.registerInjectActivateService(new SupportTicketsServlet());
    }

    @Test
    void doGetReturnsTicketListForCollectionPath() throws ServletException, IOException {
        seedTicket("967c4e7f-2620-4a67-a80a-c253f3d8aaf2");

        MockSlingHttpServletRequest request = context.request();
        MockRequestPathInfo pathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        pathInfo.setResourcePath(SupportTicketsServlet.SERVLET_PATH);
        pathInfo.setExtension("json");

        MockSlingHttpServletResponse response = context.response();
        servlet.doGet(request, response);

        assertEquals(200, response.getStatus());
        assertTrue(response.getOutputAsString().contains("\"items\""));
        assertTrue(response.getOutputAsString().contains("967c4e7f-2620-4a67-a80a-c253f3d8aaf2"));
    }

    @Test
    void doGetReturnsTicketWhenIdIsSelector() throws ServletException, IOException {
        String ticketId = "967c4e7f-2620-4a67-a80a-c253f3d8aaf2";
        seedTicket(ticketId);

        MockSlingHttpServletRequest request = context.request();
        MockRequestPathInfo pathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        pathInfo.setResourcePath(SupportTicketsServlet.SERVLET_PATH);
        pathInfo.setExtension("json");
        pathInfo.setSelectorString(ticketId);

        MockSlingHttpServletResponse response = context.response();
        servlet.doGet(request, response);

        assertEquals(200, response.getStatus());
        assertTrue(response.getOutputAsString().contains(ticketId));
        assertTrue(response.getOutputAsString().contains("Login failure"));
    }

    @Test
    void doGetReturnsCommentsWhenCommentsSelectorPresent() throws ServletException, IOException {
        String ticketId = "967c4e7f-2620-4a67-a80a-c253f3d8aaf2";
        seedTicket(ticketId);

        MockSlingHttpServletRequest request = context.request();
        MockRequestPathInfo pathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
        pathInfo.setResourcePath(SupportTicketsServlet.SERVLET_PATH);
        pathInfo.setExtension("json");
        pathInfo.setSelectorString(ticketId + ".comments");

        MockSlingHttpServletResponse response = context.response();
        servlet.doGet(request, response);

        assertEquals(200, response.getStatus());
        assertTrue(response.getOutputAsString().contains("\"items\""));
    }

    private void seedTicket(String ticketId) {
        context.create().resource(
                SupportConstants.TICKETS_PATH + "/" + ticketId,
                Map.of(
                        "title", "Login failure",
                        "description", "User cannot sign in",
                        "priority", "HIGH",
                        "status", "OPEN",
                        "assignedTo", "support-manager",
                        "createdBy", "support-agent",
                        "createdAt", "2026-08-20T00:00:00Z",
                        "updatedAt", "2026-08-20T00:00:00Z"));
    }
}
