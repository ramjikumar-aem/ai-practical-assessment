# Test Results

Date: 2026-08-28

## Command

```bash
cd src/support
mvn clean install
```

## Summary

| Module | Tests | Failures | Errors |
|---|---:|---:|---:|
| support.core | 10 | 0 | 0 |
| support.integration.tests | 11 | 0 | 0 |
| Full reactor (`support.all`) | — | — | BUILD SUCCESS |

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

## Notes

Full `mvn clean install` passes after HTL excludes, vault filter updates, repoinit fix, and removal of disallowed `SlingServletResolver` OSGi config.
