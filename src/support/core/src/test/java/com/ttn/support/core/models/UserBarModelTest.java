package com.ttn.support.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class UserBarModelTest {

    private final AemContext context = new AemContext();

    @Test
    void isAuthenticatedForSignedInUser() {
        UserBarModel model = adaptForUser("support-agent");

        assertTrue(model.isAuthenticated());
        assertEquals("support-agent", model.getUserId());
    }

    @Test
    void isNotAuthenticatedForAnonymousUser() {
        UserBarModel model = adaptForUser("anonymous");

        assertFalse(model.isAuthenticated());
        assertEquals("anonymous", model.getUserId());
    }

    @Test
    void buildsLogoutUrl() {
        UserBarModel model = adaptForUser("support-agent");

        assertEquals(
                "/content/support-tickets.logout.html?resource=%2Fcontent%2Fsupport-tickets%2Flogin.html",
                model.getLogoutUrl());
    }

    private UserBarModel adaptForUser(String userId) {
        ResourceResolver resolver = spy(context.resourceResolver());
        when(resolver.getUserID()).thenReturn(userId);
        MockSlingHttpServletRequest request = new MockSlingHttpServletRequest(resolver, context.bundleContext());
        context.create().resource("/content/support-tickets");
        request.setResource(context.resourceResolver().getResource("/content/support-tickets"));
        return request.adaptTo(UserBarModel.class);
    }
}
