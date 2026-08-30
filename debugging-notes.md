# Debugging Notes

## Archetype post-generate failure (Windows)

**Symptom:** `FileSystemException` on dispatcher `default.vhost` symlink.

**Impact:** Archetype still generated usable modules under `src/support/`.

**Mitigation:** Removed `dispatcher*` modules from parent `pom.xml` for local builds.

## Servlet PATCH support

**Symptom:** `doPatch` not available on `SlingAllMethodsServlet` parent.

**Fix:** Override `service()` and route `PATCH` to `handlePatch()`.

## Integration test classpath

**Symptom:** `NoClassDefFoundError` for Core Components internal classes when using `AppAemContext`.

**Fix:** Use plain `AemContextBuilder` with `SupportTestContext.SUPPORT_SERVICES` callback (no Core Components plugin).

## User assignee validation

**Symptom:** `UserManager` may not resolve users in AEM Mock tests.

**Mitigation:** State-machine integration tests seed tickets directly via `TicketRepository`, bypassing user validation on create.

## ui.apps HTL compile failures (adaptive forms)

**Symptom:** `mvn clean install` fails in `support.ui.apps` with missing packages such as `com.adobe.cq.forms.core.components.models.form`.

**Cause:** Archetype generated adaptive form HTL components without compile-time model JARs.

**Fix:** Exclude archetype-only adaptive form folders from HTL Java generation in `ui.apps/pom.xml`:

```xml
<excludes>
  <exclude>apps/support/components/adaptiveForm/**</exclude>
</excludes>
```

## CIF / commerce archetype cleanup

**Symptom:** `ui.content` contained unused CIF commerce site content under `/content/support`, `/var/commerce`, commerce templates, and experience fragments tied to the commerce storefront.

**Fix:** Removed CIF-related content, components, clientlibs, OSGi configs, and template references. Ticket app content now deploys only `/content/support-tickets` plus shared `conf/support` settings.

## Vault filter coverage (ui.apps / ui.content)

**Symptom:** `not covered by a filter rule` for `/apps/fd`, forms DAM content, and `/content/support-tickets`.

**Fix:** Extend `filter.xml` and `ui.apps.structure` roots for archetype-generated forms assets and ticket pages only (commerce roots removed).

## Detail page not populated after create redirect

**Symptom:** After creating a ticket, redirect to `/content/support-tickets/detail.html?id=...` shows empty fields. A manual refresh loads the ticket correctly.

**Cause:** `support.tickets` clientlib JS was included with `async=true` in `customfooterlibs.html`. On a cold navigation, the bundled script can finish loading after `DOMContentLoaded` has already fired, so `ticket-detail.js` never initialized.

**Fix:**

- Load `support.tickets` synchronously in `customfooterlibs.html` (remove `async=true`).
- Add `SupportUi.onReady()` in `support-ui.js` and use it in `ticket-list.js`, `ticket-form.js`, and `ticket-detail.js` so initialization still runs when the script arrives after the DOM is ready.

## CUG / dispatcher authentication pitfalls

**Symptom:** Anonymous user opens `/content/support-tickets.html` and sees **Authentication required** in the page instead of a login redirect.

**Cause:** The HTML page rendered, but CUG did not redirect. The ticket list JavaScript called `/bin/api/support/tickets.json`, received `401` from `AuthSupport`, and displayed the API error message.

**Fix:**

- Correct CUG packaging: single `cq:ClosedUserGroup` node with `rep:principalNames="[support-agents,support-managers,administrators]"`.
- Set `sling:authRequireLogin=true` on protected pages and `false` on `/content/support-tickets/login`.
- Deploy `SupportAuthRedirectFilter` in `core` (publish runmode) to redirect anonymous HTML requests to login.
- Client-side fallback: `support-api.js` calls `SupportUi.redirectToLogin()` on API `401`.

**Symptom:** Anonymous users see ticket list HTML, or authenticated users see stale anonymous content.

**Cause:** Dispatcher cached the page before CUG was enabled, or `/allowAuthorized` caching is misconfigured.

**Fix:**

- Deny cache for `/content/support-tickets*` and `/bin/api/support/*` in dispatcher cache rules.
- Keep `/allowAuthorized "0"` on publish farms.
- Flush dispatcher cache after deploying CUG.

**Symptom:** Login form shows **Invalid username or password** but `support-agent` exists on publish.

**Cause:** Custom `/bin/support/login` used `request.login()`, which does not authenticate repoinit JCR users on publish. Credentials were valid in the repository but the HTTP session was never established.

**Fix:** Post the login form to `/content/support-tickets/login/j_security_check` and deploy publish OSGi config `org.apache.sling.auth.form.FormAuthenticationHandler` with `form.login.form=/content/support-tickets/login.html`.

**Symptom:** Login form returns `403 Forbidden` for `/system/sling/login`.

**Cause:** Publish blocks bundled `/system/sling/login` for anonymous form posts.

**Fix:** Post to `/bin/support/login` (`SupportLoginServlet` in `core`). The servlet uses `request.login()` and redirects to the sanitized `resource` path.

**Symptom:** Login form returns `500` — `The tree for /libs/granite does not exist` at `/libs/granite/security/login`.

**Cause:** Publish (`:4503`) does not expose `/libs/granite`. The login form was posting to the Granite login servlet used on author.

**Fix:** Post the login form to `/content/support-tickets/login/j_security_check` with `j_username`, `j_password`, `_charset_`, and `resource`. Ensure dispatcher allows `POST /content/support-tickets/login/j_security_check`.

**Symptom:** Login form submits but auth fails through dispatcher.

**Cause:** Dispatcher filter blocks `/libs/granite/security/login` or `/bin/api/support/*`.

**Fix:** Add filter allow rules for `POST /content/support-tickets/login/j_security_check` and support API paths (see `dispatcher/.../filters/filters.any`).

**Symptom:** Login page itself requires login (redirect loop).

**Cause:** `cq:loginPath` not set on CUG root page, or login page path is wrong.

**Fix:** Set `cq:loginPath="/content/support-tickets/login"` on `/content/support-tickets` and replicate to publish.

**Symptom:** **Support Login** component not found in component browser.

**Cause:** Component was missing `_cq_dialog` and `jcr:description`, so it did not match other ticket components in authoring search.

**Fix:** Add `support/components/login/_cq_dialog` with `heading`, `subtitle`, and `defaultRedirectPath` fields; set `jcr:description` on the component definition.

**Symptom:** **Sign Out** does nothing, or user remains authenticated after clicking logout.

**Cause:** Logout URL points to `/system/sling/logout.html` (blocked on publish), or `SupportLogoutServlet` is not deployed.

**Fix:** Use `/content/support-tickets.logout.html?resource=/content/support-tickets/login.html` (handled by `SupportLogoutServlet`). Verify the form-auth cookie is cleared and protected pages redirect to login again.

## AEM analyser failures

**Symptom:** `SlingServletResolver: Property sling.servlet.paths - Property is not allowed` and repoinit `with password` parse error.

**Fix:**

- Remove `org.apache.sling.servlets.resolver.SlingServletResolver.cfg.json` (servlets stay registered via OSGi `@Component` properties in `core`)
- Repoinit creates paths/groups/users; verify on target SDK if password syntax is rejected in analyser
