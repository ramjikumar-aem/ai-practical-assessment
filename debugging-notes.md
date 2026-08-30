# Debugging Notes

## Issue 1: Detail page empty after create redirect

### Problem
After creating a ticket, redirect to `/content/support-tickets/detail.html?id=...` showed empty fields. Manual refresh loaded the ticket correctly.

### How I Investigated
- Reproduced create → redirect flow on author.
- Checked browser Network tab: API returned `200` with ticket data.
- Noticed `support.tickets` clientlib loaded with `async=true` in `customfooterlibs.html`.
- Confirmed `ticket-detail.js` registered `DOMContentLoaded` listener that had already fired.

### How AI Helped
AI suggested the async script timing hypothesis and proposed `SupportUi.onReady()` pattern to handle late script load.

### What I Validated
- Removed `async` from footer clientlib include.
- Added `SupportUi.onReady()` and updated list/form/detail JS to use it.
- Re-tested create → detail flow without manual refresh.

### Final Fix
- Synchronous clientlib load in `customfooterlibs.html`.
- `SupportUi.onReady()` in `support-ui.js` used by all ticket page scripts.

---

## Issue 2: Publish shows "Authentication required" instead of login redirect

### Problem
Anonymous user opened `/content/support-tickets.html` on publish and saw an API error message in the page body instead of being redirected to login.

### How I Investigated
- Confirmed HTML page rendered (CUG did not intercept before render).
- Traced `ticket-list.js` → `support-api.js` → `401` from `AuthSupport.requireAuthenticated()`.
- Reviewed CUG config in `ui.content` and publish OSGi configs.

### How AI Helped
AI recommended layered auth: CUG properties, `SupportAuthRedirectFilter` for publish HTML, client-side `redirectToLogin()` on API `401`, and publish OSGi for `FormAuthenticationHandler` / `LoginSelectorHandler`.

### What I Validated
- Deployed `ui.content`, `ui.config`, and `core` bundle to publish.
- Verified anonymous page request redirects to login.
- Verified API `401` triggers client redirect.

### Final Fix
- CUG + `sling:authRequireLogin` on protected pages; login page excluded.
- `SupportAuthRedirectFilter` (publish runmode).
- `support-api.js` calls `SupportUi.redirectToLogin()` on `401`.

---

## Issue 3: Login fails on publish (Granite / system paths)

### Problem
Login form returned `500` (`/libs/granite does not exist`), then `403` on `/system/sling/login`, then invalid credentials with custom `request.login()` servlet.

### How I Investigated
- Tested each login endpoint on publish (`:4503`).
- Read AEM publish security docs: `/libs/granite` not deployed on publish.
- Verified repoinit users exist via `curl -u support-agent:support123 .../users.json`.

### How AI Helped
AI traced Sling Form Authentication flow and recommended `POST /content/support-tickets/login/j_security_check` with `FormAuthenticationHandler` OSGi config instead of Granite or custom login servlets.

### What I Validated
- Credentials work via Basic auth curl on publish.
- Form post to `j_security_check` establishes session cookie.
- Dispatcher allows `POST` to `j_security_check`.

### Final Fix
- Login HTL form action → `/content/support-tickets/login/j_security_check`.
- Removed custom `SupportLoginServlet`.
- Publish OSGi: `org.apache.sling.auth.form.FormAuthenticationHandler`.
- Dispatcher filter allow rule for login POST.

---

## Issue 4: Redirect to `/libs/granite/core/content/login.html` on publish

### Problem
Unauthenticated publish requests redirected to Granite default login URL, which does not exist on publish.

### How I Investigated
- Opened failing URL directly on `:4503` — 404/missing tree.
- Checked `LoginSelectorHandler` default login page on publish (unset → Granite default).

### How AI Helped
AI proposed `LoginSelectorHandler` + `SlingAuthenticator` publish OSGi configs, `granite:AuthenticationRequired` mixin on content root, and `GraniteLoginRedirectFilter` as safety net.

### What I Validated
- After OSGi deploy, protected pages redirect to `/content/support-tickets/login.html`.
- Stray Granite login URLs redirect to support login.

### Final Fix
- `com.day.cq.auth.impl.LoginSelectorHandler.cfg.json` (publish).
- `granite:loginPath` on `/content/support-tickets` page node.
- `GraniteLoginRedirectFilter` in `core`.

---

## Issue 5: Logout not available after login

### Problem
Users could sign in on publish but had no way to end the session.

### How I Investigated
- Confirmed no logout UI or servlet existed.
- Ruled out `/system/sling/logout.html` (same publish restrictions as login).

### How AI Helped
AI proposed `SupportLogoutServlet` with page selector `logout` calling `Authenticator.logout()`, plus `user-bar` component on ticket pages.

### What I Validated
- Unit tests for `SupportLogoutServlet` and `UserBarModel`.
- Manual: Sign Out → login page → protected pages require login again.

### Final Fix
- `SupportLogoutServlet` on `support/components/page` selector `logout`.
- `user-bar` component on list/create/detail.
- Logout URL: `/content/support-tickets.logout.html?resource=/content/support-tickets/login.html`.

---

## Issue 6: Maven / archetype build failures

### Problem
Various `mvn clean install` failures: HTL compile errors (commerce/adaptive forms), vault filter gaps, PATCH servlet, integration test classpath, AEM analyser.

### How I Investigated
- Read Maven error output per module.
- Identified archetype-generated paths not needed for ticket app.

### How AI Helped
AI suggested HTL excludes in `ui.apps/pom.xml`, vault filter updates, `service()` override for PATCH, plain `AemContextBuilder` without Core Components plugin, and removing disallowed `SlingServletResolver` OSGi config.

### What I Validated
- Full reactor `mvn clean install` passes.
- 11 integration tests + expanded unit tests pass.

### Final Fix
- See `review-fixes.md` and individual fixes in module POMs and `ui.config`.

---

## Issue 7: Author vs publish ticket data differs

### Problem
Tickets created on author did not appear on publish (and vice versa).

### How I Investigated
- Inspected CRXDE on both instances: separate `/var/support-tickets` trees.
- Confirmed `ui.content` does not package `/var` data.

### How AI Helped
AI explained AEM author/publish repository isolation and recommended publish as single ticket runtime (or external DB for true centralization).

### What I Validated
- Created ticket on publish only → visible on publish.
- Documented in setup notes.

### Final Fix
- Documented in `database/setup-notes.md` and requirements assumptions: use publish (`:4503`) for ticket operations; seed curl examples target publish.

---

## AEM analyser failures

**Symptom:** `SlingServletResolver: Property sling.servlet.paths - Property is not allowed` and repoinit password parse errors.

**Fix:** Remove `SlingServletResolver.cfg.json`; servlets register via `@Component` properties. Verify repoinit user password syntax on target SDK.
