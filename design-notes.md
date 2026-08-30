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

## Publish Authentication (CUG)

Ticket pages under `/content/support-tickets` are protected on publish using **Closed User Group (CUG)** plus `sling:authRequireLogin`:

- CUG enabled on the list page root; `create` and `detail` inherit protection.
- CUG members are stored in `cq:ClosedUserGroupList` with `rep:principalNames="[support-agents,support-managers,administrators]"`.
- Login page: `/content/support-tickets/login` posts to **`/content/support-tickets/login/j_security_check`** (Sling Form Authentication). This validates JCR users and sets the publish auth cookie. Do not use `request.login()` or `/libs/granite`/`/system/sling` endpoints on publish.
- Publish OSGi: `org.apache.sling.auth.form.FormAuthenticationHandler` sets `form.login.form` to the content login page.
- Login component: `support/components/login` with authoring dialog (`heading`, `subtitle`, `defaultRedirectPath`).
- `SupportAuthRedirectFilter` (publish runmode only) redirects anonymous HTML requests to the login page when CUG/Sling auth does not intercept first.
- Logout: `SupportLogoutServlet` handles `GET /content/support-tickets.logout.html` (page selector on `support/components/page`) and calls `Authenticator.logout()` to clear the form-auth cookie. User bar (`support/components/user-bar`) shows **Sign Out** on list/create/detail when authenticated.

API endpoints (`/bin/api/support/*`) are **not** covered by CUG. Servlets reject `anonymous` via `AuthSupport.requireAuthenticated()`. Client-side API calls redirect to login on `401`.

## Dispatcher Cache Policy

Both `dispatcher` (Cloud Service) and `dispatcher.ams` modules:

- Allow `/bin/api/support/*` and Granite/Sling login endpoints through filters.
- Deny dispatcher cache for `/content/support-tickets*` and `/bin/api/support/*`.
- Keep `/allowAuthorized "0"` (default) — do not cache authenticated responses.

Ticket pages are accessed via `/content/support-tickets/*.html` (not short `/content/support/` vanity paths unless rewrites are added).
