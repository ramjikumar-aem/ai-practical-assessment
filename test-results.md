# Test Results

Date: 2026-08-30 (updated after auth/logout features)

## Commands

```bash
cd src/support
mvn clean install
```

```bash
cd tests
mvn clean test
```

## Summary

| Module | Status | Notes |
|---|---|---|
| `support.core` | PASS | Unit tests including transition, validation, auth, logout, models |
| `support.integration.tests` | PASS | 11 mandatory state-machine cases |
| Full reactor (`support.all`) | BUILD SUCCESS | After HTL excludes, vault filters, repoinit fix |

## Mandatory state-machine integration coverage

`TicketStatusTransitionIntegrationTest` — all 11 parameterized cases passed:

**Valid transitions (5):**

- OPEN → IN_PROGRESS
- OPEN → CANCELLED
- IN_PROGRESS → RESOLVED
- IN_PROGRESS → CANCELLED
- RESOLVED → CLOSED

**Invalid transitions (6):**

- OPEN → RESOLVED
- OPEN → CLOSED
- IN_PROGRESS → OPEN
- RESOLVED → IN_PROGRESS
- CLOSED → OPEN
- CANCELLED → OPEN

Invalid cases assert HTTP-equivalent `409 INVALID_STATUS_TRANSITION` and unchanged persisted status.

## Additional unit test coverage (post-auth/logout)

| Area | Test classes |
|---|---|
| Auth | `AuthSupportTest`, `SupportAuthPathsTest`, `SupportLoginRedirectTest` |
| Logout | `SupportLogoutServletTest`, `UserBarModelTest` |
| Models | `LoginModelTest`, `TicketListModelTest`, `TicketFormModelTest`, `TicketDetailModelTest` |
| API | `SupportTicketsServletTest`, `SupportApiRouteParserTest` |
| Domain | `StatusTransitionServiceTest`, `TicketValidatorTest` |

## Notes

- Full `mvn clean install` passes after HTL excludes, vault filter updates, repoinit fix, and removal of disallowed `SlingServletResolver` OSGi config.
- Manual publish verification (login, logout, CUG) documented in `database/setup-notes.md` — recommended before submission demo.
