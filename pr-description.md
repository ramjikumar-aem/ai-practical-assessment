# PR Description

## Summary

Implements an AEMaaCS Support Ticket Management System with JCR-backed persistence, REST-style Sling servlets, HTL UI components, publish authentication (CUG + form login + logout), dispatcher rules, and mandatory lifecycle integration tests.

## Features Implemented

- Ticket CRUD: create, list, detail, update fields
- Status state machine with backend enforcement
- Comments: add and list per ticket
- Keyword search and status filter
- AEM User Management integration for assignees
- Publish login page with Sling Form Authentication
- Publish logout with user bar (Sign Out)
- CUG protection on ticket pages
- API authentication (reject anonymous)
- Dispatcher allow/cache rules for support app

## Technical Changes

### Core (`support.core`)
- Domain models, `TicketRepository`, `CommentRepository`
- `TicketService`, `CommentService`, `StatusTransitionService`, `TicketValidator`
- `SupportTicketsServlet`, `SupportUsersServlet`, `SupportLogoutServlet`
- `AuthSupport`, `SupportAuthRedirectFilter`, `GraniteLoginRedirectFilter`
- Sling models: `TicketListModel`, `TicketFormModel`, `TicketDetailModel`, `LoginModel`, `UserBarModel`

### UI (`support.ui.apps`)
- Components: `ticket-list`, `ticket-form`, `ticket-detail`, `login`, `user-bar`, `page`
- Clientlib `support.tickets` (CSS/JS) with `SupportApi` and `SupportUi` helpers

### Content (`support.ui.content`)
- Pages: `/content/support-tickets`, `/create`, `/detail`, `/login`
- CUG configuration and content ACLs

### Config (`support.ui.config`)
- Repoinit: `/var/support-tickets` structure, groups, users, ACLs
- Publish OSGi: `FormAuthenticationHandler`, `LoginSelectorHandler`, `SlingAuthenticator`

### Dispatcher
- Filter allow: `/bin/api/support/*`, login `j_security_check`
- Cache deny: `/content/support-tickets*`, `/bin/api/support/*`

### Tests (`tests/`)
- `TicketStatusTransitionIntegrationTest` — 11 cases (5 valid, 6 invalid)
- Expanded unit tests in `core` (20+ test classes)

## Database Changes

- JCR schema under `/var/support-tickets/tickets` and `/comments` (see `database/schema-or-migrations/jcr-schema.md`)
- Repoinit creates structure, sample users, and ACLs
- No SQL/external database
- **Note:** Author and publish have separate `/var` repositories; use publish for ticket runtime

## Testing Done

- [x] `mvn clean install` — BUILD SUCCESS
- [x] `support.core` unit tests — pass
- [x] `TicketStatusTransitionIntegrationTest` — 11/11 pass
- [x] `SupportLogoutServletTest`, `UserBarModelTest`, auth path tests — pass
- [ ] Manual publish UI: login, CRUD, transitions, comments, logout (recommended before demo)

## AI Usage Summary

Cursor Agent used for architecture, implementation, debugging publish auth, documentation, and test authoring. Human validated all changes via Maven, integration tests, and manual AEM SDK verification. See [ai-prompts/](ai-prompts/) and [reflection.md](reflection.md).

## Screenshots / Demo Notes

**Demo flow on publish (`http://localhost:4503`):**

1. Open `/content/support-tickets.html` → redirect to login
2. Sign in as `support-agent` / `support123`
3. List tickets, search/filter, create ticket
4. Detail: update fields, change status, add comment
5. Sign Out → back to login; protected pages require login again

**API smoke test:**
```cmd
curl -u support-agent:support123 http://localhost:4503/bin/api/support/tickets.json
```

## Known Limitations

- Ticket data not shared between author and publish (separate JCR `/var` per instance)
- Keyword search is in-memory (no Oak index)
- No pagination on ticket list
- Dispatcher modules may be excluded from local Windows build (symlink issue)
- Manual UI E2E not automated in CI

## Future Improvements

- External database for cross-instance data centralization
- Oak Query for search at scale
- Pagination, sorting, role-based transition permissions
- Automated UI tests against publish
- Content Distribution for config-only sync if multi-publish needed
