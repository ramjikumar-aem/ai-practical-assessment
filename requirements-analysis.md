# Requirement Analysis

## Selected Project Option

**AEM as a Cloud Service (AEMaaCS)** — Support Ticket Management System using the latest AEM SDK (archetype 57), HTL frontend, OSGi backend, and JCR persistence.

## My Understanding (in your own words)

The assignment asks for a full-stack ticket management application on AEM where support agents can create, search, update, and lifecycle-manage tickets with comments. The backend must enforce a strict status state machine; the UI must reflect backend outcomes clearly. Data must survive restarts. AEM content (`ui.content`) deploys pages and components; transactional ticket data lives outside content packages under `/var/support-tickets`. Publish requires authentication (CUG + form login) so anonymous users cannot access ticket pages or APIs. The deliverable includes working code, tests (especially lifecycle integration tests), documentation, and evidence of responsible AI-assisted development.

## Functional Requirements

### Users
- Integrate with AEM User Management (`id`, `name`, `email`, `role`).
- Tickets reference users via `createdBy` and `assignedTo`.
- Expose assignable users via API for UI dropdowns.

### Tickets
- Fields: `id`, `title`, `description`, `priority`, `status`, `assignedTo`, `createdBy`, `createdAt`, `updatedAt`.
- Create, list, view detail, update mutable fields.
- Change status only through the defined state machine.
- Search by keyword (title/description).
- Filter by status.

### Comments
- Fields: `id`, `ticketId`, `message`, `createdBy`, `createdAt`.
- Add comments to existing tickets; list comments per ticket.

### Lifecycle
| Current | Allowed next |
|---|---|
| OPEN | IN_PROGRESS, CANCELLED |
| IN_PROGRESS | RESOLVED, CANCELLED |
| RESOLVED | CLOSED |
| CLOSED | (none) |
| CANCELLED | (none) |

### Publish authentication
- Protect ticket pages on publish with CUG / auth requirements.
- Custom login page with form-based authentication.
- Logout clears session and returns user to login.
- API rejects anonymous callers.

## Non-Functional Requirements

- Persistence across AEM restart (JCR under `/var/support-tickets`).
- Backend validation for required fields, enums, user references, and transitions.
- Structured JSON error responses (`code`, `message`, optional `fields`).
- Meaningful UI loading, empty, validation, and error states.
- Mandatory integration tests for valid and invalid status transitions.
- Dispatcher rules: allow API and login POST; do not cache authenticated pages.
- Documentation and AI workflow artifacts per assessment brief.
- Build and test via Maven (`mvn clean install`).

## Assumptions

- **Publish is the runtime environment** for ticket operations; author and publish each have separate `/var` repositories (not replicated).
- Priority values: `LOW`, `MEDIUM`, `HIGH`.
- New tickets default to `OPEN` status; `createdBy` comes from the authenticated AEM session user.
- Keyword search is in-memory filter (acceptable at assessment scale).
- Seed users (`support-agent`, `support-manager`) are created via repoinit or manually in User Admin.
- No external database; JCR is the persistence layer.
- Dispatcher modules are configured but may be excluded from local parent POM on Windows due to symlink issues.

## Clarifications (questions for a product owner)

1. Should author and publish share ticket data (would require replication or external DB)?
2. Is pagination/sorting required for the ticket list?
3. Should managers be the only role allowed to close tickets, or is any authenticated agent sufficient?
4. Are email notifications on status change in scope?
5. Should the login page be authorable only on publish, or also previewable on author?

## Edge Cases

- Creating a ticket with a non-existent `assignedTo` user → validation error.
- Transition from terminal state (`CLOSED`, `CANCELLED`) → `409 INVALID_STATUS_TRANSITION`, status unchanged.
- Comment on missing ticket → `404 NOT_FOUND`.
- Anonymous API access → `401 UNAUTHORIZED`; UI redirects to login.
- Login with invalid credentials → error message on login page.
- Login page accessed while already authenticated → allowed; logout returns to login.
- Empty search/filter → returns all tickets (or empty list if none exist).
- Author vs publish ticket data divergence when tickets created on different instances.
- Script loading after `DOMContentLoaded` on client-side navigation → detail page empty until refresh (fixed with `SupportUi.onReady()`).
- `/libs/granite` login URLs on publish → 404/500; must use content login + `j_security_check`.

## Acceptance Criteria

See [acceptance-criteria.md](acceptance-criteria.md) for the full checklist mapped to implementation and test evidence.
