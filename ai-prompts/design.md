# Design Prompts

## Prompt 1: Architecture and persistence

**Prompt (summary):**  
Map entities to JCR under `/var/support-tickets`. Use AEM User Management for users. REST API at `/bin/api/support`. Central StatusTransitionService state machine.

**AI response summary:**  
Layered architecture: HTL → servlets → services → repositories. JCR ticket/comment nodes. Selector-based servlet routing. Structured error model.

**Accepted:**  
Repository pattern, `StatusTransitionService` as single transition authority, `SupportApiRouteParser` for URL routing.

**Changed:**  
Added `AuthSupport` layer when publish security was added later.

**Rejected:**  
Duplicating user entities in JCR — assignment requires AEM User Management integration.

---

## Prompt 2: Publish authentication design

**Prompt (summary):**  
Design publish login for ticket pages using CUG. Protect `/content/support-tickets`. Custom login page. API must reject anonymous.

**AI response summary:**  
CUG on content root, login component, `FormAuthenticationHandler` OSGi, `SupportAuthRedirectFilter`, API `401` + client redirect.

**Accepted:**  
CUG + form auth + filter + API auth combination.

**Changed:**  
Added `LoginSelectorHandler`, `granite:AuthenticationRequired` mixin, `GraniteLoginRedirectFilter` after Granite login URL failures.

**Rejected:**  
Using `/libs/granite/security/login` on publish — tree does not exist on publish instance.

---

## Prompt 3: Logout design

**Prompt (summary):**  
Implement logout on publish without `/system/sling/logout.html`. User bar with Sign Out on ticket pages.

**AI response summary:**  
`SupportLogoutServlet` with page selector `logout` calling `Authenticator.logout()`. `UserBarModel` + hidden `user-bar` component.

**Accepted:**  
Full logout plan as implemented.

**Changed:**  
None.

**Rejected:**  
`/system/sling/logout.html` — same publish restrictions as system login.
