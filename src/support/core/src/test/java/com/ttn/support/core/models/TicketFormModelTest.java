package com.ttn.support.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
class TicketFormModelTest {

    private final AemContext context = new AemContext();

    @Test
    void usesDefaultsWhenDialogPropertiesMissing() {
        context.create().resource("/content/component");
        TicketFormModel model = context.currentResource("/content/component").adaptTo(TicketFormModel.class);

        assertEquals("Create Ticket", model.getHeading());
        assertEquals("/content/support-tickets.html", model.getListPagePath());
        assertEquals("/content/support-tickets/detail.html", model.getDetailPagePath());
    }

    @Test
    void usesAuthoredListPagePath() {
        context.create().resource("/content/component",
                "listPagePath", "/content/support-tickets",
                "detailPagePath", "/content/support-tickets/detail");
        TicketFormModel model = context.currentResource("/content/component").adaptTo(TicketFormModel.class);

        assertEquals("/content/support-tickets.html", model.getListPagePath());
        assertEquals("/content/support-tickets/detail.html", model.getDetailPagePath());
    }
}
