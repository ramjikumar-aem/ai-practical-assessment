package com.ttn.support.core.util;

import org.apache.sling.api.request.RequestPathInfo;

public final class SupportApiRouteParser {

    public static final String API_BASE_PATH = "/bin/api/support";
    public static final String TICKETS_PATH = API_BASE_PATH + "/tickets";
    public static final String JSON_EXTENSION = "json";

    private SupportApiRouteParser() {
    }

    public static TicketRoute resolve(RequestPathInfo pathInfo) {
        if (pathInfo == null || !JSON_EXTENSION.equals(pathInfo.getExtension())) {
            return TicketRoute.unknown();
        }

        String[] selectors = pathInfo.getSelectors();
        if (selectors == null || selectors.length == 0) {
            return TicketRoute.collection();
        }

        String ticketId = selectors[0];
        if (selectors.length == 1) {
            return TicketRoute.ticket(ticketId);
        }
        if (selectors.length == 2 && "status".equals(selectors[1])) {
            return TicketRoute.status(ticketId);
        }
        if (selectors.length == 2 && "comments".equals(selectors[1])) {
            return TicketRoute.comments(ticketId);
        }
        return TicketRoute.unknown();
    }

    public static final class TicketRoute {

        public enum Type {
            COLLECTION,
            TICKET,
            STATUS,
            COMMENTS,
            UNKNOWN
        }

        private final Type type;
        private final String ticketId;

        private TicketRoute(Type type, String ticketId) {
            this.type = type;
            this.ticketId = ticketId;
        }

        public static TicketRoute collection() {
            return new TicketRoute(Type.COLLECTION, null);
        }

        public static TicketRoute ticket(String ticketId) {
            return new TicketRoute(Type.TICKET, ticketId);
        }

        public static TicketRoute status(String ticketId) {
            return new TicketRoute(Type.STATUS, ticketId);
        }

        public static TicketRoute comments(String ticketId) {
            return new TicketRoute(Type.COMMENTS, ticketId);
        }

        public static TicketRoute unknown() {
            return new TicketRoute(Type.UNKNOWN, null);
        }

        public boolean isCollection() {
            return type == Type.COLLECTION;
        }

        public boolean isTicket() {
            return type == Type.TICKET;
        }

        public boolean isStatus() {
            return type == Type.STATUS;
        }

        public boolean isComments() {
            return type == Type.COMMENTS;
        }

        public boolean isUnknown() {
            return type == Type.UNKNOWN;
        }

        public String ticketId() {
            return ticketId;
        }
    }
}
