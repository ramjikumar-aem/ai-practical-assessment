package com.ttn.support.core.repository;

import com.ttn.support.core.constants.SupportConstants;
import com.ttn.support.core.models.Comment;
import com.ttn.support.core.util.DateTimeUtil;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.osgi.service.component.annotations.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component(service = CommentRepository.class)
public class CommentRepository {

    public Comment create(ResourceResolver resolver, Comment comment) throws PersistenceException {
        ensureCommentsRoot(resolver);
        String id = comment.getId() != null ? comment.getId() : UUID.randomUUID().toString();
        Resource resource = resolver.getResource(SupportConstants.COMMENTS_PATH);
        Resource commentResource = resolver.create(resource, id, null);
        comment.setId(id);
        writeComment(commentResource, comment);
        resolver.commit();
        return comment;
    }

    public List<Comment> findByTicketId(ResourceResolver resolver, String ticketId) {
        Resource root = resolver.getResource(SupportConstants.COMMENTS_PATH);
        if (root == null) {
            return List.of();
        }
        return StreamSupport.stream(root.getChildren().spliterator(), false)
                .map(this::readComment)
                .filter(comment -> ticketId.equals(comment.getTicketId()))
                .sorted(Comparator.comparing(Comment::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Optional<Comment> findById(ResourceResolver resolver, String id) {
        Resource resource = resolver.getResource(SupportConstants.COMMENTS_PATH + "/" + id);
        if (resource == null || ResourceUtil.isNonExistingResource(resource)) {
            return Optional.empty();
        }
        return Optional.of(readComment(resource));
    }

    private void ensureCommentsRoot(ResourceResolver resolver) throws PersistenceException {
        if (resolver.getResource(SupportConstants.ROOT_PATH) == null) {
            resolver.create(resolver.getResource("/var"), "support-tickets", null);
        }
        if (resolver.getResource(SupportConstants.COMMENTS_PATH) == null) {
            resolver.create(resolver.getResource(SupportConstants.ROOT_PATH), "comments", null);
        }
    }

    private Comment readComment(Resource resource) {
        Comment comment = new Comment();
        comment.setId(resource.getName());
        comment.setTicketId(resource.getValueMap().get("ticketId", String.class));
        comment.setMessage(resource.getValueMap().get("message", String.class));
        comment.setCreatedBy(resource.getValueMap().get("createdBy", String.class));
        comment.setCreatedAt(DateTimeUtil.parse(resource.getValueMap().get("createdAt", String.class)));
        return comment;
    }

    private void writeComment(Resource resource, Comment comment) throws PersistenceException {
        ModifiableValueMap properties = resource.adaptTo(ModifiableValueMap.class);
        if (properties == null) {
            throw new PersistenceException("Unable to adapt comment resource");
        }
        properties.put("ticketId", comment.getTicketId());
        properties.put("message", comment.getMessage());
        properties.put("createdBy", comment.getCreatedBy());
        properties.put("createdAt", DateTimeUtil.format(comment.getCreatedAt() == null ? Instant.now() : comment.getCreatedAt()));
    }
}
