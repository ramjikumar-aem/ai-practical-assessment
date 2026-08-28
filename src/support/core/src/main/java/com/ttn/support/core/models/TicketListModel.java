package com.ttn.support.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TicketListModel {

    @ValueMapValue
    @Default(values = "Support Tickets")
    private String heading;

    @ValueMapValue
    @Default(values = "/content/support-tickets/create.html")
    private String createPagePath;

    public String getHeading() {
        return heading;
    }

    public String getCreatePagePath() {
        if (createPagePath == null || createPagePath.isBlank()) {
            return "/content/support-tickets/create.html";
        }
        return createPagePath.endsWith(".html") ? createPagePath : createPagePath + ".html";
    }
}
