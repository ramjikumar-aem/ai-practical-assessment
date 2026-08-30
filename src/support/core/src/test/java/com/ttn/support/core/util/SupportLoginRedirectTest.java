package com.ttn.support.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SupportLoginRedirectTest {

    @Test
    void usesDefaultWhenResourceMissing() {
        assertEquals("/content/support-tickets.html", SupportLoginRedirect.sanitize(null));
        assertEquals("/content/support-tickets.html", SupportLoginRedirect.sanitize("  "));
    }

    @Test
    void allowsSupportTicketPathsOnly() {
        assertEquals(
                "/content/support-tickets/detail.html?id=1",
                SupportLoginRedirect.sanitize("/content/support-tickets/detail.html?id=1"));
        assertEquals("/content/support-tickets.html", SupportLoginRedirect.sanitize("/content/other.html"));
    }
}
