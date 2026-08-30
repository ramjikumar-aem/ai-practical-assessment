package com.ttn.support.core.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SupportAuthPathsTest {

    @Test
    void protectsTicketPagesButNotLogin() {
        assertTrue(SupportAuthPaths.isProtectedContentPath("/content/support-tickets"));
        assertTrue(SupportAuthPaths.isProtectedContentPath("/content/support-tickets/create"));
        assertTrue(SupportAuthPaths.isProtectedContentPath("/content/support-tickets/detail"));
        assertFalse(SupportAuthPaths.isProtectedContentPath("/content/support-tickets/login"));
        assertFalse(SupportAuthPaths.isProtectedContentPath("/content/other"));
    }

    @Test
    void buildsLoginRedirectWithResourceParameter() {
        String redirectUrl = SupportAuthPaths.buildLoginRedirectUrl(
                "/content/support-tickets",
                "html",
                null);

        assertEquals(
                "/content/support-tickets/login.html?resource=%2Fcontent%2Fsupport-tickets.html",
                redirectUrl);
    }

    @Test
    void buildsLogoutUrlWithLoginPageResource() {
        assertEquals(
                "/content/support-tickets.logout.html?resource=%2Fcontent%2Fsupport-tickets%2Flogin.html",
                SupportAuthPaths.buildLogoutUrl());
    }
}
