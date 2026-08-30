package com.ttn.support.core.servlets;

import com.ttn.support.core.filters.SupportAuthPaths;
import com.ttn.support.core.util.SupportLoginRedirect;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.auth.Authenticator;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = Servlet.class)
@SlingServletResourceTypes(
        resourceTypes = "support/components/page",
        selectors = "logout",
        extensions = "html",
        methods = HttpConstants.METHOD_GET)
@ServiceDescription("Support Tickets publish logout")
public class SupportLogoutServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private transient Authenticator authenticator;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        String resource = request.getParameter("resource");
        if (resource == null || resource.isBlank()) {
            resource = SupportAuthPaths.DEFAULT_LOGIN_PAGE;
        } else {
            resource = SupportLoginRedirect.sanitize(resource);
        }
        request.setAttribute("resource", resource);
        authenticator.logout(request, response);
    }
}
