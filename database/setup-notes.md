# Database Setup Notes

## Persistence model

Support tickets use **JCR nodes** under `/var/support-tickets`. Data survives AEM restarts because it lives in the repository, not in-memory state.

## Initial setup

1. Build and deploy packages from `src/support`:
   ```bash
   mvn clean install -PautoInstallSinglePackage
   ```
2. Repoinit creates:
   - `/var/support-tickets/tickets`
   - `/var/support-tickets/comments`
   - groups `support-agents` and `support-managers`
   - ACL on `/var/support-tickets` for those groups (and `administrators`)
   - users `support-agent` and `support-manager` (when repoinit password syntax is accepted on your SDK)
3. `ui.content` installs read ACL on `/content/support-tickets` for support groups and CUG configuration (login page + closed user groups).
4. If seed users are missing, create them in **AEM Security** (`/useradmin`) and assign groups from the table below.
5. If tickets return API `404` while nodes exist in CRXDE, repoinit ACL likely did not run. Redeploy `ui.config`, or set ACL manually on `/var/support-tickets` for `support-agents` and `support-managers`.
6. Verify assignable users before seeding tickets:
   ```cmd
   curl -u support-agent:support123 http://localhost:4502/bin/api/support/users
   ```
   Response must include `support-manager` and `support-agent`.
7. Load optional seed tickets using commands in `database/seed-data/README.md`.

## Publish + dispatcher verification

1. Deploy to **author** (`mvn clean install -PautoInstallSinglePackage`).
2. **Replicate** (or install on publish `:4503`):
   - `/content/support-tickets` (login page + CUG config)
   - `/conf/support`
   - `/apps/support` and core bundle
3. Deploy `ui.config` to publish (repoinit groups/users + `/var/support-tickets` ACL).
4. Deploy dispatcher config from `src/support/dispatcher` and `src/support/dispatcher.ams` to the publish dispatcher.

### Test via publish / dispatcher

| Step | Command / action | Expected |
|---|---|---|
| Anonymous page access | `GET /content/support-tickets.html` | Redirect to `/content/support-tickets/login.html?resource=...` |
| Login | Sign in as `support-agent` / `support123` on login page | Redirect back to requested page |
| Component browser | Search **Support Login** in **Support Tickets - Content** | Component appears and can be dragged onto a page |
| Anonymous API | `curl http://publish:4503/bin/api/support/tickets.json` | `401 UNAUTHORIZED` (browser UI also redirects to login) |
| Authenticated API | `curl -u support-agent:support123 http://publish:4503/bin/api/support/tickets.json` | `200` with ticket list |
| Dispatcher cache | Check dispatcher cache directory after page hits | No cached HTML under `/content/support-tickets*` |

Use full paths (`/content/support-tickets/*.html`); short `/content/support/` vanity URLs are not mapped to ticket pages by default.

Login on publish uses `POST /content/support-tickets/login/j_security_check` (Sling Form Authentication). Verify credentials with `curl -u support-agent:support123 http://localhost:4503/bin/api/support/users.json` on publish before testing the form.

## User management

Users are native AEM users resolved through `UserManager`:

| User | Password | Group | Role |
|---|---|---|---|
| support-agent | support123 | support-agents | agent |
| support-manager | support123 | support-managers | manager |
| admin | admin | administrators | admin |

`createdBy` and `assignedTo` store AEM user ids.

## Verification

1. Create a ticket through UI or API.
2. Restart AEM SDK.
3. Confirm ticket still appears in list/detail views.
