# Design Notes

## Architecture

The proposed architecture separates:
- AEM-rendered frontend components/pages;
- backend API endpoints;
- application services for ticket, comment, validation, and lifecycle logic;
- a persistence adapter;
- AEM User Management integration.

This separation keeps the status state machine independent of transport and UI code.

## Status State Machine

Use a centralized transition map:

- `OPEN`: `IN_PROGRESS`, `CANCELLED`
- `IN_PROGRESS`: `RESOLVED`, `CANCELLED`
- `RESOLVED`: `CLOSED`
- `CLOSED`: no transitions
- `CANCELLED`: no transitions

The transition service should:
1. load the current ticket;
2. verify the requested target status;
3. reject illegal transitions before persistence;
4. update status and `updatedAt` atomically within the persistence boundary where supported.

## Persistence Design

Ticket and comment data is stored in JCR under `/var/support-tickets` (see `database/schema-or-migrations/jcr-schema.md`). This path is created via repoinit and is **not** deployed through `ui.content`.

## API Base Path

Implemented servlets:

- `GET/POST/PATCH /bin/api/support/tickets.json`
- `GET /bin/api/support/users`

Selector routing examples:

- `GET /bin/api/support/tickets.{id}.json`
- `POST /bin/api/support/tickets.{id}.status.json`
- `GET/POST /bin/api/support/tickets.{id}.comments.json`

## Priority Values

`LOW`, `MEDIUM`, `HIGH`

## User Role Mapping

| AEM group | Role string |
|---|---|
| administrators | admin |
| support-managers | manager |
| (default) | agent |

## Error Model

Use a consistent structured error response containing:
- machine-readable error code;
- human-readable message;
- optional field-level validation details.

Suggested categories:
- validation error;
- not found;
- invalid state transition;
- conflict;
- unexpected server error.

## UI Design

The list is the primary entry point. Detail is the primary workspace. Lifecycle actions should be context-sensitive, but backend validation remains authoritative. Errors should appear near the affected action or form and should not silently discard user input.

## AEM-Specific Boundary

AEM pages/components handle presentation and interaction. Java/Sling backend layers own request handling and domain behavior. Persistence access is isolated so implementation details do not leak into UI or API code.

## Clientlib Loading

Ticket UI scripts (`support.tickets`) are included from `support/components/page/customfooterlibs.html` without `async` so page init runs reliably after create → detail redirects. Component HTL does not embed clientlibs; CSS/JS are loaded at page level via `customheaderlibs.html` / `customfooterlibs.html`. Use `SupportUi.onReady()` for DOM-dependent initialization when scripts may load after `DOMContentLoaded`.
