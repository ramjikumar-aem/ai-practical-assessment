package com.ttn.support.core.filters;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.engine.EngineConstants;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Component(
        service = Filter.class,
        property = {
                EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
                EngineConstants.SLING_FILTER_PATTERN + "=/content/support-tickets(/.*)?",
                EngineConstants.SLING_FILTER_EXTENSIONS + "=html",
                Constants.SERVICE_RANKING + ":Integer=-500"
        })
@ServiceDescription("Redirect anonymous publish users to the support login page")
public class SupportAuthRedirectFilter implements Filter {

    @Reference
    private transient SlingSettingsService slingSettingsService;

    @Override
    public void init(FilterConfig filterConfig) {
        // no-op
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof SlingHttpServletRequest) || !(response instanceof SlingHttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        if (!isPublish()) {
            chain.doFilter(request, response);
            return;
        }

        SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;
        String resourcePath = slingRequest.getRequestPathInfo().getResourcePath();

        if (!SupportAuthPaths.isProtectedContentPath(resourcePath) || isAuthenticated(slingRequest)) {
            chain.doFilter(request, response);
            return;
        }

        String redirectUrl = SupportAuthPaths.buildLoginRedirectUrl(
                resourcePath,
                slingRequest.getRequestPathInfo().getExtension(),
                slingRequest.getQueryString());
        slingResponse.sendRedirect(redirectUrl);
    }

    @Override
    public void destroy() {
        // no-op
    }

    private boolean isPublish() {
        return slingSettingsService != null && slingSettingsService.getRunModes().contains("publish");
    }

    private boolean isAuthenticated(SlingHttpServletRequest request) {
        String userId = request.getResourceResolver().getUserID();
        return userId != null && !userId.isBlank() && !"anonymous".equals(userId);
    }
}
