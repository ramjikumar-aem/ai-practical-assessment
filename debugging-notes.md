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

## AEM analyser failures

**Symptom:** `SlingServletResolver: Property sling.servlet.paths - Property is not allowed` and repoinit `with password` parse error.

**Fix:**

- Remove `org.apache.sling.servlets.resolver.SlingServletResolver.cfg.json` (servlets stay registered via OSGi `@Component` properties in `core`)
- Repoinit creates paths/groups/users; verify on target SDK if password syntax is rejected in analyser
