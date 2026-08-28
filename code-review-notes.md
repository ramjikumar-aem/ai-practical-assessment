# Code Review Notes

## Strengths

- State machine centralized in `StatusTransitionService` (single source of truth)
- Backend returns structured JSON errors (`code`, `message`, `fields`)
- Transactional ticket data isolated under `/var/support-tickets` (not in `ui.content`)
- Mandatory integration tests cover all required valid/invalid transitions

## Findings

| Severity | Item | Recommendation |
|---|---|---|
| Low | Search implemented via in-memory filter | Acceptable for assessment scale; add Oak index/query for production volume |
| Low | CSRF token optional in clientlib | Verify Granite CSRF token in full AEM SDK manual test |
| Info | Dispatcher modules removed locally | Re-enable for Cloud Manager pipeline if dispatcher validation required |

## Security

- Servlet paths allowlisted in `SlingServletResolver` OSGi config
- No credentials in repository
- Uses AEM session user for `createdBy`
