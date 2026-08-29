package com.ttn.support.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TicketFormModel {

    private static final String DEFAULT_LIST_PAGE = "/content/support-tickets.html";
    private static final String DEFAULT_DETAIL_PAGE = "/content/support-tickets/detail.html";

    @ValueMapValue
    @Default(values = "Create Ticket")
    private String heading;

    @ValueMapValue
    @Default(values = "/content/support-tickets")
    private String listPagePath;

    @ValueMapValue
    @Default(values = "/content/support-tickets/detail")
    private String detailPagePath;

    public String getHeading() {
        return heading;
    }

    public String getListPagePath() {
        return PagePathSupport.toHtmlPath(listPagePath, DEFAULT_LIST_PAGE);
    }

    public String getDetailPagePath() {
        return PagePathSupport.toHtmlPath(detailPagePath, DEFAULT_DETAIL_PAGE);
    }
}
