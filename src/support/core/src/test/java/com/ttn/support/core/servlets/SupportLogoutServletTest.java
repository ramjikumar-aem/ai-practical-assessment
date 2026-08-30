package com.ttn.support.core.servlets;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.auth.Authenticator;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SupportLogoutServletTest {

    private final AemContext context = new AemContext();

    @Mock
    private Authenticator authenticator;

    private SupportLogoutServlet servlet;

    @BeforeEach
    void setUp() {
        context.registerService(Authenticator.class, authenticator);
        servlet = context.registerInjectActivateService(new SupportLogoutServlet());
        context.create().page("/content/support-tickets");
    }

    @Test
    void logoutUsesLoginPageWhenResourceMissing() throws ServletException, IOException {
        MockSlingHttpServletRequest request = context.request();
        request.setResource(context.resourceResolver().getResource("/content/support-tickets/jcr:content"));
        MockSlingHttpServletResponse response = context.response();

        servlet.doGet(request, response);

        ArgumentCaptor<MockSlingHttpServletRequest> requestCaptor =
                ArgumentCaptor.forClass(MockSlingHttpServletRequest.class);
        verify(authenticator).logout(requestCaptor.capture(), org.mockito.Mockito.eq(response));
        assertEquals("/content/support-tickets/login.html", requestCaptor.getValue().getAttribute("resource"));
    }

    @Test
    void logoutSanitizesResourceParameter() throws ServletException, IOException {
        MockSlingHttpServletRequest request = context.request();
        request.setResource(context.resourceResolver().getResource("/content/support-tickets/jcr:content"));
        request.addRequestParameter("resource", "/content/support-tickets/login.html");
        MockSlingHttpServletResponse response = context.response();

        servlet.doGet(request, response);

        ArgumentCaptor<MockSlingHttpServletRequest> requestCaptor =
                ArgumentCaptor.forClass(MockSlingHttpServletRequest.class);
        verify(authenticator).logout(requestCaptor.capture(), org.mockito.Mockito.eq(response));
        assertEquals("/content/support-tickets/login.html", requestCaptor.getValue().getAttribute("resource"));
    }

    @Test
    void logoutRejectsExternalResourceParameter() throws ServletException, IOException {
        MockSlingHttpServletRequest request = context.request();
        request.setResource(context.resourceResolver().getResource("/content/support-tickets/jcr:content"));
        request.addRequestParameter("resource", "https://evil.example/phish");
        MockSlingHttpServletResponse response = context.response();

        servlet.doGet(request, response);

        ArgumentCaptor<MockSlingHttpServletRequest> requestCaptor =
                ArgumentCaptor.forClass(MockSlingHttpServletRequest.class);
        verify(authenticator).logout(requestCaptor.capture(), org.mockito.Mockito.eq(response));
        assertEquals("/content/support-tickets.html", requestCaptor.getValue().getAttribute("resource"));
    }
}
