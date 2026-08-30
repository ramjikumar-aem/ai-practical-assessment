package com.ttn.support.core.util;

import com.ttn.support.core.exception.SupportApiException;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthSupportTest {

    @Test
    void requireAuthenticatedRejectsAnonymous() {
        SlingHttpServletRequest request = mockRequest("anonymous");

        SupportApiException exception = assertThrows(
                SupportApiException.class,
                () -> AuthSupport.requireAuthenticated(request));

        assertEquals(401, exception.getStatus());
        assertEquals("UNAUTHORIZED", exception.getCode());
    }

    @Test
    void requireAuthenticatedRejectsBlankUser() {
        SlingHttpServletRequest request = mockRequest("  ");

        assertThrows(SupportApiException.class, () -> AuthSupport.requireAuthenticated(request));
    }

    @Test
    void requireAuthenticatedAllowsSignedInUser() {
        SlingHttpServletRequest request = mockRequest("support-agent");

        assertDoesNotThrow(() -> AuthSupport.requireAuthenticated(request));
    }

    private SlingHttpServletRequest mockRequest(String userId) {
        SlingHttpServletRequest request = mock(SlingHttpServletRequest.class);
        ResourceResolver resourceResolver = mock(ResourceResolver.class);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.getUserID()).thenReturn(userId);
        return request;
    }
}
