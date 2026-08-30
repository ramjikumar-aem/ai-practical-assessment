package com.ttn.support.core.models;

import com.ttn.support.core.filters.SupportAuthPaths;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

@Model(adaptables = SlingHttpServletRequest.class)
public class UserBarModel {

    @SlingObject
    private SlingHttpServletRequest request;

    public boolean isAuthenticated() {
        String userId = getUserId();
        return userId != null && !userId.isBlank() && !"anonymous".equals(userId);
    }

    public String getUserId() {
        if (request == null || request.getResourceResolver() == null) {
            return null;
        }
        return request.getResourceResolver().getUserID();
    }

    public String getLogoutUrl() {
        return SupportAuthPaths.buildLogoutUrl();
    }
}
