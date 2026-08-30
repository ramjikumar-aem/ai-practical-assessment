package com.ttn.support.core.util;

import com.ttn.support.core.exception.SupportApiException;
import org.apache.sling.api.SlingHttpServletRequest;

public final class AuthSupport {

    private AuthSupport() {
    }

    public static void requireAuthenticated(SlingHttpServletRequest request) throws SupportApiException {
        String userId = request.getResourceResolver().getUserID();
        if (userId == null || userId.isBlank() || "anonymous".equals(userId)) {
            throw new SupportApiException("UNAUTHORIZED", "Authentication required", 401);
        }
    }
}
