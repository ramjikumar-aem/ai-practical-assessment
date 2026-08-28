package com.ttn.support.core.service;

import com.ttn.support.core.constants.SupportConstants;
import com.ttn.support.core.exception.NotFoundException;
import com.ttn.support.core.exception.ValidationException;
import com.ttn.support.core.models.UserRef;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component(service = UserService.class)
public class UserService {

    private static final Set<String> ASSIGNABLE_GROUP_IDS = Set.of(
            "support-agents",
            "support-managers",
            "administrators");

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    public Optional<UserRef> findById(ResourceResolver resolver, String userId) throws RepositoryException {
        if (userId == null || userId.isBlank()) {
            return Optional.empty();
        }
        try (ResourceResolver serviceResolver = getUserReaderResolver()) {
            return findByIdInternal(serviceResolver, userId);
        } catch (LoginException ex) {
            return findByIdInternal(resolver, userId);
        }
    }

    public void validateAssignee(ResourceResolver resolver, String userId)
            throws RepositoryException, ValidationException {
        Optional<UserRef> user = findById(resolver, userId);
        if (user.isEmpty()) {
            throw new ValidationException(
                    "assignedTo user not found",
                    Map.of("assignedTo", "user not found"));
        }
    }

    public List<UserRef> listAssignableUsers(ResourceResolver resolver) throws RepositoryException {
        try (ResourceResolver serviceResolver = getUserReaderResolver()) {
            return listAssignableUsersInternal(serviceResolver);
        } catch (LoginException ex) {
            return listAssignableUsersInternal(resolver);
        }
    }

    public UserRef getCurrentUser(ResourceResolver resolver, String userId) throws RepositoryException, NotFoundException {
        return findById(resolver, userId).orElseGet(() -> new UserRef(userId, userId, "", "agent"));
    }

    private Optional<UserRef> findByIdInternal(ResourceResolver resolver, String userId) throws RepositoryException {
        UserManager userManager = resolver.adaptTo(UserManager.class);
        if (userManager == null) {
            return Optional.empty();
        }
        Authorizable authorizable = userManager.getAuthorizable(userId);
        if (authorizable == null || authorizable.isGroup() || !isAssignable((User) authorizable)) {
            return Optional.empty();
        }
        return Optional.of(toUserRef((User) authorizable));
    }

    private List<UserRef> listAssignableUsersInternal(ResourceResolver resolver) throws RepositoryException {
        UserManager userManager = resolver.adaptTo(UserManager.class);
        List<UserRef> users = new ArrayList<>();
        if (userManager == null) {
            return users;
        }
        Iterator<Authorizable> iterator = userManager.findAuthorizables(
                "jcr:primaryType",
                "rep:User",
                UserManager.SEARCH_TYPE_AUTHORIZABLE);
        while (iterator.hasNext()) {
            Authorizable authorizable = iterator.next();
            if (!authorizable.isGroup() && isAssignable((User) authorizable)) {
                users.add(toUserRef((User) authorizable));
            }
        }
        users.sort((left, right) -> String.valueOf(left.getName()).compareToIgnoreCase(String.valueOf(right.getName())));
        return users;
    }

    private boolean isAssignable(User user) throws RepositoryException {
        Iterator<Group> groups = user.memberOf();
        while (groups.hasNext()) {
            if (ASSIGNABLE_GROUP_IDS.contains(groups.next().getID())) {
                return true;
            }
        }
        return false;
    }

    private ResourceResolver getUserReaderResolver() throws LoginException {
        Map<String, Object> authInfo = new HashMap<>();
        authInfo.put(ResourceResolverFactory.SUBSERVICE, SupportConstants.SUBSERVICE_USER_READER);
        return resourceResolverFactory.getServiceResourceResolver(authInfo);
    }

    private UserRef toUserRef(User user) throws RepositoryException {
        String id = user.getID();
        String name = user.getProperty("./profile/givenName") != null
                ? user.getProperty("./profile/givenName")[0].getString()
                : id;
        String email = user.getProperty("./profile/email") != null
                ? user.getProperty("./profile/email")[0].getString()
                : "";
        return new UserRef(id, name, email, resolveRole(user));
    }

    private String resolveRole(User user) throws RepositoryException {
        if (isMemberOf(user, "administrators")) {
            return "admin";
        }
        if (isMemberOf(user, "support-managers")) {
            return "manager";
        }
        return "agent";
    }

    private boolean isMemberOf(User user, String groupId) throws RepositoryException {
        Iterator<Group> groups = user.memberOf();
        while (groups.hasNext()) {
            if (groupId.equals(groups.next().getID())) {
                return true;
            }
        }
        return false;
    }
}
