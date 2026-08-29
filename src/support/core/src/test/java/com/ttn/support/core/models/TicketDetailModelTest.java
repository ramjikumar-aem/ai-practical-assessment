package com.ttn.support.core.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
class TicketDetailModelTest {

    private final AemContext context = new AemContext();

    @Test
    void usesDefaultsWhenDialogPropertiesMissing() {
        context.create().resource("/content/component");
        TicketDetailModel model = context.currentResource("/content/component").adaptTo(TicketDetailModel.class);

        assertEquals("Ticket Detail", model.getHeading());
        assertEquals("/content/support-tickets.html", model.getListPagePath());
        assertEquals("/content/support-tickets/detail.html", model.getDetailPagePath());
    }

    @Test
    void usesAuthoredPagePaths() {
        context.create().resource("/content/component",
                "listPagePath", "/content/support-tickets",
                "detailPagePath", "/content/support-tickets/detail");
        TicketDetailModel model = context.currentResource("/content/component").adaptTo(TicketDetailModel.class);

        assertEquals("/content/support-tickets.html", model.getListPagePath());
        assertEquals("/content/support-tickets/detail.html", model.getDetailPagePath());
    }
}
