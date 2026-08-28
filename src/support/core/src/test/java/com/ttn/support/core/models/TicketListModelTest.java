package com.ttn.support.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
class TicketListModelTest {

    private final AemContext context = new AemContext();

    @Test
    void usesDefaultsWhenDialogPropertiesMissing() {
        context.create().resource("/content/component");
        TicketListModel model = context.currentResource("/content/component").adaptTo(TicketListModel.class);

        assertEquals("Support Tickets", model.getHeading());
        assertEquals("/content/support-tickets/create.html", model.getCreatePagePath());
    }

    @Test
    void usesAuthoredDialogProperties() {
        context.create().resource("/content/component",
                "heading", "My Tickets",
                "createPagePath", "/content/support-tickets/create");
        TicketListModel model = context.currentResource("/content/component").adaptTo(TicketListModel.class);

        assertEquals("My Tickets", model.getHeading());
        assertEquals("/content/support-tickets/create.html", model.getCreatePagePath());
    }
}
