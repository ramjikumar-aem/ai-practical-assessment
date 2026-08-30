# Code Review Notes

## AI-Assisted Review Summary

AI was used to review:
- State machine centralization (`StatusTransitionService` as single source of truth).
- Servlet auth (`AuthSupport.requireAuthenticated()` on all API endpoints).
- Publish login approach (reject `/libs/granite`, `/system/sling`, custom `request.login()`).
- Clientlib loading strategy (sync + `onReady`).
- Dispatcher filter/cache rules for authenticated content.
- Test coverage for mandatory transition matrix.

AI flagged risks: open redirect on login `resource` param, caching authenticated pages, and author/publish data isolation.

## My Review Observations

### Strengths
- Clean layering: servlets → services → repositories.
- Structured JSON errors with consistent `code`/`message`/`fields`.
- Transactional data isolated under `/var/support-tickets` (not in content packages).
- Mandatory integration tests cover all required valid/invalid transitions.
- Publish auth uses standard Sling Form Authentication patterns.
- Logout uses `Authenticator.logout()` via page selector (publish-safe).

### Areas reviewed manually
- HTL component dialogs and authored path defaults.
- Repoinit ACL coverage for `support-agents` / `support-managers`.
- CUG `rep:principalNames` packaging (single `cq:ClosedUserGroup` node).
- Dispatcher allow/deny rules aligned with auth endpoints.

| Severity | Item | Notes |
|---|---|---|
| Low | In-memory keyword search | Acceptable for assessment; Oak query + index for production |
| Low | No pagination on ticket list | Not in brief; add if PO requires |
| Info | Author/publish `/var` isolation | Documented; publish is runtime |
| Info | Dispatcher modules excluded locally on Windows | Re-enable for Cloud Manager pipeline |

## Changes Made After Review

- Added `SupportLoginRedirect.sanitize()` to prevent open redirects on login `resource` param.
- Switched login from custom servlet to `j_security_check`.
- Added publish OSGi configs for form auth and login selector.
- Added `SupportAuthRedirectFilter` and API `401` client redirect.
- Fixed async clientlib → sync + `SupportUi.onReady()`.
- Added `SupportLogoutServlet` and `user-bar` component.
- Expanded unit tests for auth paths, logout, and user bar model.
- Updated documentation artifacts to match assessment template.

## Suggestions Rejected (and why)

| AI suggestion | Rejected because |
|---|---|
| Store tickets under `/content` for replication | Transactional data should not be content-managed; `/var` is correct |
| Use `/system/sling/logout.html` on publish | Blocked/unreliable on publish, same as `/system/sling/login` |
| Custom `/bin/support/login` with `request.login()` | Does not authenticate repoinit JCR users on publish |
| Replicate `/var/support-tickets` author→publish | Non-standard, conflict-prone; documented publish-only runtime instead |
| External database in v1 | Out of assessment scope; JCR persistence meets restart requirement |
| CSRF token required in every API call | Session/Basic auth sufficient for assessment; form login uses Granite CSRF via Sling |

## Security

- No credentials in repository or documentation.
- API rejects anonymous users.
- Login redirect `resource` param sanitized to `/content/support-tickets/*` only.
- Dispatcher denies cache for authenticated paths.
- Servlets registered via OSGi `@Component` (no disallowed `sling.servlet.paths` OSGi config).
