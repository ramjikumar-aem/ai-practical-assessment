# Reflection

## What I Built

An AEMaaCS Support Ticket Management application with:
- HTL UI (list, create, detail, login, user bar) on `/content/support-tickets`
- REST-style Sling servlets at `/bin/api/support/*`
- JCR persistence under `/var/support-tickets`
- Centralized status state machine with mandatory integration tests
- Publish authentication (CUG, form login, API auth, logout)
- Dispatcher filter/cache rules for Cloud Service and AMS modules
- Full assessment documentation and AI workflow artifacts

## How I Used AI (across the lifecycle)

| Phase | Usage |
|---|---|
| Planning | Drafted requirements analysis, API contract, implementation plan from assignment brief |
| Design | JCR schema, service layering, publish auth architecture |
| Implementation | Generated Java services/servlets, HTL components, OSGi configs, clientlibs |
| Testing | Integration test scaffolding, unit test expansion |
| Debugging | Diagnosed HTL compile errors, CUG/login issues, async JS timing, Granite login on publish |
| Documentation | Markdown artifacts, prompt history, PR description |
| Review | Security and acceptance-criteria traceability checks |

**Primary tool:** Cursor Agent with AEM-focused skills and project rules.

## What AI Helped With Most

- **Publish authentication** — navigating Sling Form Auth, CUG, OSGi configs, and why Granite paths fail on publish.
- **Build fixes** — archetype cleanup (commerce/adaptive forms HTL excludes, vault filters).
- **Boilerplate velocity** — servlet routing, Sling models, test scaffolding.
- **Documentation structure** — organizing assessment artifacts to match the required template.

## What AI Got Wrong

- Initially suggested `/libs/granite/security/login` and `/system/sling/login` for publish login — both fail on publish.
- Proposed custom `/bin/support/login` with `request.login()` — credentials valid but session never established for repoinit users.
- Some debugging notes retained superseded fixes until manually consolidated.
- Occasionally suggested replicating `/var` content — not standard for transactional data.

## How I Validated AI Output

- Ran `mvn clean install` after every significant change.
- Executed integration tests (11 transition cases) and expanded unit test suite.
- Manual verification on AEM SDK author (`:4502`) and publish (`:4503`).
- `curl` tests for API auth, user listing, and ticket CRUD.
- Cross-checked Adobe docs and community patterns for publish login/logout.
- Rejected suggestions that conflicted with AEM Cloud Service constraints (e.g. `sling.servlet.paths` OSGi config).

## What I Would Improve Next

- Oak Query + index for search at scale.
- Pagination and sorting on ticket list.
- External database if author and publish must share ticket data.
- Automated UI tests (Cypress/Playwright) against publish.
- Servlet-level HTTP integration tests for full API contract.
- CSRF verification on form login in manual test checklist.

## Reusable Workflow (prompts, rules, specs, templates)

1. **Start with assignment brief → requirements-analysis.md** before coding.
2. **Freeze API contract** early; implement servlets against it.
3. **Use assessment doc templates** (this repo structure) as a checklist.
4. **Prompt AI with stack context:** "AEMaaCS SDK, publish `:4503`, JCR `/var`, no external DB."
5. **Always ask AI to run Maven tests** after backend changes.
6. **For publish auth:** explicitly state "do not use `/libs/granite` or `/system/sling` on publish."
7. **Record prompts by activity** in `ai-prompts/` with accept/reject rationale.
8. **Cursor rules** in `.cursor/rules` for code style and branch safety.
9. **Debugging template:** Problem → Investigate → AI help → Validate → Fix (see `debugging-notes.md`).
