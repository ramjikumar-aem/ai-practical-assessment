package com.ttn.support.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
class LoginModelTest {

    private final AemContext context = new AemContext();

    @Test
    void usesDefaultsWhenDialogPropertiesMissing() {
        context.create().resource("/content/component");
        LoginModel model = context.currentResource("/content/component").adaptTo(LoginModel.class);

        assertEquals("Support Tickets", model.getHeading());
        assertEquals("Sign in to manage support tickets.", model.getSubtitle());
        assertEquals("/content/support-tickets.html", model.getDefaultRedirectPath());
    }

    @Test
    void usesAuthoredProperties() {
        context.create().resource("/content/component",
                "heading", "Support Portal",
                "subtitle", "Please sign in.",
                "defaultRedirectPath", "/content/support-tickets/detail");
        LoginModel model = context.currentResource("/content/component").adaptTo(LoginModel.class);

        assertEquals("Support Portal", model.getHeading());
        assertEquals("Please sign in.", model.getSubtitle());
        assertEquals("/content/support-tickets/detail.html", model.getDefaultRedirectPath());
    }
}
