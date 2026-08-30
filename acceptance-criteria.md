# Acceptance Criteria

## Core

- [x] Create a ticket with title, description, priority, and assignee; persisted with `id`, timestamps, and `OPEN` status.
- [x] List all persisted tickets via API and ticket-list UI.
- [x] View ticket detail by ID; missing ticket returns meaningful not-found handling.
- [x] Update title, description, priority, and assignee; `updatedAt` changes on success.
- [x] Enforce status transitions: OPEN→IN_PROGRESS, OPEN→CANCELLED, IN_PROGRESS→RESOLVED, IN_PROGRESS→CANCELLED, RESOLVED→CLOSED.
- [x] Reject all other transitions with backend error; persisted status unchanged.
- [x] Add comments to existing tickets; list comments on detail view.
- [x] Keyword search matches title and description.
- [x] Status filter returns only matching tickets.
- [x] Publish login page protects ticket pages (CUG + `sling:authRequireLogin`).
- [x] Authenticated users can sign out; session cleared and login required again.

## Validation

- [x] Missing required ticket fields rejected with `VALIDATION_ERROR` and field details.
- [x] Invalid priority value rejected.
- [x] Invalid or non-assignable `assignedTo` user rejected.
- [x] Empty comment message rejected.
- [x] Invalid status filter value handled per API contract.
- [x] Anonymous API requests rejected with `401 UNAUTHORIZED`.

## Error Handling

- [x] API returns structured JSON errors: `code`, `message`, optional `fields`.
- [x] Illegal status transition returns `409 INVALID_STATUS_TRANSITION`.
- [x] Not found returns `404 NOT_FOUND`.
- [x] UI shows inline validation and action-level errors without silent data loss.
- [x] API `401` triggers client redirect to login page.
- [x] Login form shows error on invalid credentials.

## Testing

- [x] Unit tests for `StatusTransitionService`, `TicketValidator`, route parser, auth helpers, Sling models.
- [x] Integration tests: 5 valid + 6 invalid transitions (`TicketStatusTransitionIntegrationTest`).
- [x] Invalid transitions verified to leave persisted status unchanged.
- [x] `mvn clean install` passes (core + integration tests).
- [x] Test results recorded in [test-results.md](test-results.md).
- [ ] Full manual UI pass on publish after latest auth/logout deploy (recommended before submission).

## Documentation

- [x] README with setup and build instructions.
- [x] API contract ([api-contract.md](api-contract.md)).
- [x] Design notes ([design-notes.md](design-notes.md)).
- [x] Database/setup notes ([database/setup-notes.md](database/setup-notes.md)).
- [x] UI flow ([ui-flow.md](ui-flow.md)).
- [x] Test strategy ([test-strategy.md](test-strategy.md)).
- [x] Debugging notes ([debugging-notes.md](debugging-notes.md)).
- [x] AI prompt history under [ai-prompts/](ai-prompts/).
- [x] Reflection, PR description, code review notes.
