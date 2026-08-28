package com.ttn.support.core.servlets;

import com.ttn.support.core.exception.SupportApiException;
import com.ttn.support.core.models.Comment;
import com.ttn.support.core.models.Ticket;
import com.ttn.support.core.service.CommentService;
import com.ttn.support.core.service.TicketService;
import com.ttn.support.core.util.JsonResponseWriter;
import com.ttn.support.core.util.SupportApiRouteParser;
import com.ttn.support.core.util.SupportApiRouteParser.TicketRoute;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.json.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=" + SupportApiRouteParser.TICKETS_PATH,
                "sling.servlet.extensions=" + SupportApiRouteParser.JSON_EXTENSION,
                "sling.servlet.methods=GET,POST,PATCH"
        })
@ServiceDescription("Support Tickets API")
public class SupportTicketsServlet extends SlingAllMethodsServlet {

    static final String SERVLET_PATH = SupportApiRouteParser.TICKETS_PATH;

    private static final long serialVersionUID = 1L;

    @Reference
    private transient TicketService ticketService;

    @Reference
    private transient CommentService commentService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        try {
            TicketRoute route = SupportApiRouteParser.resolve(request.getRequestPathInfo());
            if (route.isUnknown()) {
                writeNotFound(response, "Endpoint not found");
                return;
            }
            if (route.isCollection()) {
                List<Ticket> tickets = ticketService.list(
                        request.getResourceResolver(),
                        request.getParameter("q"),
                        request.getParameter("status"));
                writeJson(response, SlingHttpServletResponse.SC_OK, JsonResponseWriter.ticketsList(tickets));
                return;
            }
            if (route.isComments()) {
                List<Comment> comments = commentService.listComments(request.getResourceResolver(), route.ticketId());
                writeJson(response, SlingHttpServletResponse.SC_OK, JsonResponseWriter.commentsList(comments));
                return;
            }
            Ticket ticket = ticketService.getById(request.getResourceResolver(), route.ticketId());
            writeJson(response, SlingHttpServletResponse.SC_OK, JsonResponseWriter.toTicketJson(ticket));
        } catch (SupportApiException ex) {
            writeError(response, ex);
        } catch (Exception ex) {
            writeUnexpected(response, ex);
        }
    }

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        try {
            TicketRoute route = SupportApiRouteParser.resolve(request.getRequestPathInfo());
            if (route.isUnknown()) {
                writeNotFound(response, "Endpoint not found");
                return;
            }
            JsonObject payload = JsonResponseWriter.readObject(readBody(request));
            if (route.isCollection()) {
                Ticket created = ticketService.create(
                        request.getResourceResolver(),
                        payload,
                        request.getResourceResolver().getUserID());
                writeJson(response, SlingHttpServletResponse.SC_CREATED, JsonResponseWriter.toTicketJson(created));
                return;
            }
            if (route.isStatus()) {
                Ticket updated = ticketService.transitionStatus(request.getResourceResolver(), route.ticketId(), payload);
                writeJson(response, SlingHttpServletResponse.SC_OK, JsonResponseWriter.toTicketJson(updated));
                return;
            }
            if (route.isComments()) {
                Comment created = commentService.addComment(
                        request.getResourceResolver(),
                        route.ticketId(),
                        payload,
                        request.getResourceResolver().getUserID());
                writeJson(response, SlingHttpServletResponse.SC_CREATED, JsonResponseWriter.toCommentJson(created));
                return;
            }
            writeNotFound(response, "Endpoint not found");
        } catch (SupportApiException ex) {
            writeError(response, ex);
        } catch (Exception ex) {
            writeUnexpected(response, ex);
        }
    }

    @Override
    protected void service(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(request.getMethod())) {
            handlePatch(request, response);
            return;
        }
        super.service(request, response);
    }

    private void handlePatch(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        try {
            TicketRoute route = SupportApiRouteParser.resolve(request.getRequestPathInfo());
            if (!route.isTicket()) {
                writeNotFound(response, "Ticket id is required");
                return;
            }
            Ticket updated = ticketService.update(
                    request.getResourceResolver(),
                    route.ticketId(),
                    JsonResponseWriter.readObject(readBody(request)),
                    request.getResourceResolver().getUserID());
            writeJson(response, SlingHttpServletResponse.SC_OK, JsonResponseWriter.toTicketJson(updated));
        } catch (SupportApiException ex) {
            writeError(response, ex);
        } catch (Exception ex) {
            writeUnexpected(response, ex);
        }
    }

    private String readBody(SlingHttpServletRequest request) throws IOException {
        return new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private void writeJson(SlingHttpServletResponse response, int status, JsonObject body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(body.toString());
    }

    private void writeNotFound(SlingHttpServletResponse response, String message) throws IOException {
        writeJson(response, SlingHttpServletResponse.SC_NOT_FOUND,
                JsonResponseWriter.error("NOT_FOUND", message));
    }

    private void writeError(SlingHttpServletResponse response, SupportApiException exception) throws IOException {
        writeJson(response, exception.getStatus(), JsonResponseWriter.error(exception));
    }

    private void writeUnexpected(SlingHttpServletResponse response, Exception exception) throws IOException {
        writeJson(response, SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                JsonResponseWriter.error("INTERNAL_ERROR",
                        exception.getMessage() == null ? "Unexpected error" : exception.getMessage()));
    }
}
