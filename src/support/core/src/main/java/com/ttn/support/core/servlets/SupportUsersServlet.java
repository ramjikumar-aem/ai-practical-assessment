package com.ttn.support.core.servlets;

import com.ttn.support.core.exception.SupportApiException;
import com.ttn.support.core.models.UserRef;
import com.ttn.support.core.service.UserService;
import com.ttn.support.core.util.AuthSupport;
import com.ttn.support.core.util.JsonResponseWriter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.paths=/bin/api/support/users",
                "sling.servlet.methods=GET"
        })
@ServiceDescription("Support Users API")
public class SupportUsersServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private transient UserService userService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        try {
            AuthSupport.requireAuthenticated(request);
            List<UserRef> users = userService.listAssignableUsers(request.getResourceResolver());
            response.setStatus(SlingHttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(JsonResponseWriter.usersList(users).toString());
        } catch (SupportApiException ex) {
            response.setStatus(ex.getStatus());
            response.setContentType("application/json");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(JsonResponseWriter.error(ex).toString());
        } catch (Exception ex) {
            response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write(JsonResponseWriter.error("INTERNAL_ERROR",
                    ex.getMessage() == null ? "Unexpected error" : ex.getMessage()).toString());
        }
    }
}
