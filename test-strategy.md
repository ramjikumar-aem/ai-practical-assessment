# Test Strategy

## Test Scope

In scope:
- Status state machine (mandatory integration tests)
- Ticket/comment validation
- API route parsing and auth rejection
- Sling model defaults
- Servlet error mapping
- Logout servlet and auth path helpers

Out of scope (documented):
- Full browser E2E automation (manual verification on AEM SDK)
- Dispatcher integration tests in CI
- Load/performance testing
- Cross-instance author/publish data sync

## Unit Tests

Location: `src/support/core/src/test/java`

| Area | Test class | Coverage |
|---|---|---|
| State machine | `StatusTransitionServiceTest` | Allowed/blocked transitions, terminal states |
| Validation | `TicketValidatorTest` | Required fields, priority enum |
| Route parser | `SupportApiRouteParserTest` | Selector routing for tickets/status/comments |
| Auth | `AuthSupportTest` | Anonymous rejection |
| JSON errors | `JsonResponseWriterTest` | Error envelope shape |
| Login redirect | `SupportLoginRedirectTest` | Open-redirect sanitization |
| Auth paths | `SupportAuthPathsTest` | Protected paths, login/logout URL builders |
| Sling models | `TicketListModelTest`, `TicketFormModelTest`, `TicketDetailModelTest`, `LoginModelTest`, `UserBarModelTest` | Dialog defaults, logout URL |
| Servlets | `SupportTicketsServletTest`, `SupportLogoutServletTest` | Anonymous rejection, logout flow |
| Archetype samples | `HelloWorldModelTest`, `LoggingFilterTest`, etc. | Archetype boilerplate |

## Component Tests

- HTL components are not unit-tested in isolation; validated via AEM SDK manual render and `htl-maven-plugin` compile during `ui.apps` build.
- Clientlib JS (`ticket-list.js`, `ticket-form.js`, `ticket-detail.js`) verified manually and via API integration from browser.

## API / Integration Tests

Location: `tests/src/test/java/com/ttn/support/it`

**`TicketStatusTransitionIntegrationTest`** — 11 parameterized cases:

Valid (5):
- OPEN → IN_PROGRESS
- OPEN → CANCELLED
- IN_PROGRESS → RESOLVED
- IN_PROGRESS → CANCELLED
- RESOLVED → CLOSED

Invalid (6):
- OPEN → RESOLVED, OPEN → CLOSED
- IN_PROGRESS → OPEN
- RESOLVED → IN_PROGRESS
- CLOSED → OPEN, CANCELLED → OPEN

Each invalid case asserts `409 INVALID_STATUS_TRANSITION` and unchanged persisted status.

**`SupportTicketsServletTest`** — anonymous `401`, authenticated list/detail.

## Edge Case Tests

| Edge case | How tested |
|---|---|
| Terminal state transition | Integration test (CLOSED/CANCELLED → OPEN) |
| Open redirect on login `resource` param | `SupportLoginRedirectTest` |
| Anonymous API access | `AuthSupportTest`, `SupportTicketsServletTest` |
| Unknown API route | `SupportApiRouteParserTest` |
| Logout with missing/malicious `resource` | `SupportLogoutServletTest` |

## Tests Not Covered (and why)

| Gap | Reason |
|---|---|
| Full servlet HTTP integration for every API endpoint | Covered by service-layer integration tests + manual SDK verification; adding full HTTP mock suite is diminishing returns for assessment scope |
| Publish CUG/login browser tests | Requires running AEM publish + manual or Cypress setup not in CI |
| Author vs publish data consistency | Architectural decision (separate `/var` per instance); documented, not automated |
| Oak index/query performance | In-memory search sufficient at current scale |
| CSRF token on form login | Granite form auth handles; optional manual verify |

## Exit Criteria

- `mvn clean install` passes.
- All 11 mandatory transition integration tests pass.
- Results recorded in [test-results.md](test-results.md).
- Manual publish verification checklist in [database/setup-notes.md](database/setup-notes.md).

## Traceability

| Requirement | Evidence |
|---|---|
| State machine | `TicketStatusTransitionIntegrationTest` |
| Validation | `TicketValidatorTest`, servlet tests |
| Auth | `AuthSupportTest`, publish manual checklist |
| Persistence | Restart verification (manual) + JCR repository tests via integration seed |
| UI flows | `ui-flow.md` + manual SDK test |
