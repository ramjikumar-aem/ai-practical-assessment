package com.ttn.support.core.util;

public final class SupportLoginRedirect {

    private static final String DEFAULT_RESOURCE = "/content/support-tickets.html";

    private SupportLoginRedirect() {
    }

    public static String sanitize(String resource) {
        if (resource == null || resource.isBlank()) {
            return DEFAULT_RESOURCE;
        }
        String normalized = resource.trim();
        if (!normalized.startsWith("/content/support-tickets")) {
            return DEFAULT_RESOURCE;
        }
        return normalized;
    }
}
