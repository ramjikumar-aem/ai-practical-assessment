package com.ttn.support.core.testcontext;

import com.ttn.support.core.repository.CommentRepository;
import com.ttn.support.core.repository.TicketRepository;
import com.ttn.support.core.service.CommentService;
import com.ttn.support.core.service.StatusTransitionService;
import com.ttn.support.core.service.TicketService;
import com.ttn.support.core.service.UserService;
import com.ttn.support.core.validation.CommentValidator;
import com.ttn.support.core.validation.TicketValidator;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextCallback;

public final class SupportTestContext {

    private SupportTestContext() {
    }

    public static final AemContextCallback SUPPORT_SERVICES = new AemContextCallback() {
        @Override
        public void execute(AemContext context) {
            context.create().resource("/var/support-tickets");
            context.create().resource("/var/support-tickets/tickets");
            context.create().resource("/var/support-tickets/comments");

            context.registerInjectActivateService(new TicketRepository());
            context.registerInjectActivateService(new CommentRepository());
            context.registerInjectActivateService(new StatusTransitionService());
            context.registerInjectActivateService(new TicketValidator());
            context.registerInjectActivateService(new CommentValidator());
            context.registerInjectActivateService(new UserService());
            context.registerInjectActivateService(new TicketService());
            context.registerInjectActivateService(new CommentService());
        }
    };
}
