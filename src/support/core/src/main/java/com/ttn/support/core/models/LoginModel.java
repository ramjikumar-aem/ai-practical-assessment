package com.ttn.support.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LoginModel {

    private static final String DEFAULT_REDIRECT_PATH = "/content/support-tickets.html";

    @ValueMapValue
    @Default(values = "Support Tickets")
    private String heading;

    @ValueMapValue
    @Default(values = "Sign in to manage support tickets.")
    private String subtitle;

    @ValueMapValue
    @Default(values = "/content/support-tickets")
    private String defaultRedirectPath;

    public String getHeading() {
        return heading;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getDefaultRedirectPath() {
        return PagePathSupport.toHtmlPath(defaultRedirectPath, DEFAULT_REDIRECT_PATH);
    }
}
