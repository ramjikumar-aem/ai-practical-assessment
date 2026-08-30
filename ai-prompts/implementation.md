# Implementation Prompts

## Prompt 1: Core backend and servlets

**Prompt (summary):**  
Generate archetype 57 project with appId `support`. Implement TicketRepository, CommentRepository, servlets, HTL components, repoinit, clientlibs for API calls.

**AI response summary:**  
Generated core bundle with full CRUD, status transitions, comments, `/bin/api/support/tickets.json` routing via selectors.

**Accepted:**  
Service/repository layering, servlet registration via `@Component`, JSON error writer.

**Changed:**  
PATCH handled via `service()` override (parent lacks `doPatch`).

**Rejected:**  
`SlingServletResolver` OSGi with `sling.servlet.paths` — AEM analyser disallows in Cloud Service.

---

## Prompt 2: HTL components and clientlibs

**Prompt (summary):**  
Implement ticket-list, ticket-form, ticket-detail HTL with dialogs. Clientlib for API integration. Page-level clientlib loading.

**AI response summary:**  
Components with Sling models, `support.tickets` clientlib, `customheaderlibs`/`customfooterlibs` on page component.

**Accepted:**  
Page-level clientlibs, component dialogs for authored paths.

**Changed:**  
Moved clientlibs from component body to page level after CSS not loading.

**Rejected:**  
Async JS loading — caused detail page init failure after redirect.

---

## Prompt 3: Publish login component

**Prompt (summary):**  
Create support/components/login with dialog. Login page at /content/support-tickets/login. Form posts to j_security_check.

**AI response summary:**  
Login HTL, LoginModel, _cq_dialog, content page, FormAuthenticationHandler publish config.

**Accepted:**  
Login component in component browser with jcr:description and dialog.

**Changed:**  
Form action evolved: granite login → system/sling → custom servlet → j_security_check (final).

**Rejected:**  
Custom SupportLoginServlet with request.login() — invalid credentials on publish despite correct repoinit users.

---

## Prompt 4: Logout implementation

**Prompt (summary):**  
Implement logout per plan: SupportLogoutServlet, UserBarModel, user-bar component, CSS, docs.

**AI response summary:**  
Implemented servlet, model, component, included in ticket headers, unit tests, documentation updates.

**Accepted:**  
All items from logout plan.

**Changed:**  
None.

**Rejected:**  
None.

---

## Prompt 5: Archetype cleanup

**Prompt (summary):**  
Fix full reactor build: exclude archetype commerce/adaptiveForm HTL from htl-maven-plugin compile in ui.apps. Remove CIF content.

**AI response summary:**  
HTL excludes, vault filter updates, commerce content removal from ui.content.

**Accepted:**  
HTL excludes and CIF cleanup.

**Changed:**  
Extended filter.xml for forms assets still required by archetype.

**Rejected:**  
Keeping unused commerce components — caused compile and packaging failures.
