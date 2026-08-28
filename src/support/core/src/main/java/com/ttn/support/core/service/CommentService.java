package com.ttn.support.core.service;

import com.ttn.support.core.exception.NotFoundException;
import com.ttn.support.core.exception.SupportApiException;
import com.ttn.support.core.models.Comment;
import com.ttn.support.core.repository.CommentRepository;
import com.ttn.support.core.validation.CommentValidator;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.json.JsonObject;
import java.time.Instant;
import java.util.List;

@Component(service = CommentService.class)
public class CommentService {

    @Reference
    private CommentRepository commentRepository;

    @Reference
    private TicketService ticketService;

    @Reference
    private CommentValidator commentValidator;

    public Comment addComment(ResourceResolver resolver, String ticketId, JsonObject payload, String currentUserId)
            throws SupportApiException, PersistenceException {
        commentValidator.validateCreate(payload, currentUserId);
        ticketService.getById(resolver, ticketId);

        Comment comment = new Comment();
        comment.setTicketId(ticketId);
        comment.setMessage(payload.getString("message"));
        comment.setCreatedBy(currentUserId);
        comment.setCreatedAt(Instant.now());
        return commentRepository.create(resolver, comment);
    }

    public List<Comment> listComments(ResourceResolver resolver, String ticketId) throws NotFoundException {
        ticketService.getById(resolver, ticketId);
        return commentRepository.findByTicketId(resolver, ticketId);
    }
}
