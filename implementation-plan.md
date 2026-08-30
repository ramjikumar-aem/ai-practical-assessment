# Implementation Plan

## Overview

Build an AEMaaCS Support Ticket Management application with a layered architecture: HTL components + clientlibs (frontend), Sling servlets + OSGi services (backend), JCR repositories under `/var/support-tickets` (persistence), and repoinit for structure/users/ACLs. Deliver mandatory lifecycle integration tests, publish authentication, and full assessment documentation.

## Task Breakdown

| # | Task | Module | Status |
|---|---|---|---|
| 1 | Generate AEM archetype 57 project (`support`) | `src/support` | Done |
| 2 | Define domain models, JCR schema, repoinit | `core`, `ui.config` | Done |
| 3 | Implement repositories + services + state machine | `core` | Done |
| 4 | Implement `/bin/api/support` servlets | `core` | Done |
| 5 | HTL components: list, form, detail, login, user-bar | `ui.apps` | Done |
| 6 | Content pages under `/content/support-tickets` | `ui.content` | Done |
| 7 | Clientlibs + API integration JS | `ui.apps` | Done |
| 8 | Unit + integration tests | `core`, `tests/` | Done |
| 9 | CUG + publish login (form auth, filters, OSGi) | `ui.content`, `ui.config`, `core` | Done |
| 10 | Dispatcher filter/cache rules | `dispatcher`, `dispatcher.ams` | Done |
| 11 | Logout servlet + user bar | `core`, `ui.apps` | Done |
| 12 | Assessment documentation + AI artifacts | repo root | Done |

## Milestones

| Milestone | Target | Evidence |
|---|---|---|
| M1 — Project bootstrap | Day 1 | Archetype generated, Maven reactor builds |
| M2 — Backend + persistence | Day 1–2 | Servlets, repoinit, integration tests green |
| M3 — Frontend UI | Day 2 | List/create/detail flows working on author |
| M4 — Publish auth | Day 2–3 | Login, CUG, API 401, dispatcher rules |
| M5 — Logout + polish | Day 3 | Sign out, docs, test-results |
| M6 — Submission | Day 3 | All artifacts complete, candidate-info filled |

## AI Usage Plan

| Phase | AI role | Human role |
|---|---|---|
| Planning | Draft requirements analysis, API contract, implementation plan | Validate against assignment brief |
| Design | Propose JCR schema, service layering, auth approach | Choose publish-only data model, reject `/system/sling` on publish |
| Implementation | Generate Java, HTL, OSGi config, clientlibs | Review diffs, run Maven, fix AEM-specific issues |
| Testing | Author unit/integration test scaffolding | Verify state machine coverage matches brief |
| Debugging | Diagnose HTL compile, CUG, login, async JS issues | Reproduce on SDK, validate fixes manually |
| Documentation | Draft markdown artifacts and prompt history | Edit for accuracy, add personal reflection |

## Risks

| Risk | Impact |
|---|---|
| Archetype bloat (commerce, adaptive forms) | Build failures, vault filter gaps |
| Publish vs author data isolation | Different ticket data per instance |
| Granite login unavailable on publish | Login broken on `:4503` |
| Async clientlib timing | Detail page empty after create redirect |
| Repoinit password syntax on analyser | User seeding may fail in CI |
| Dispatcher symlink on Windows | Local build may exclude dispatcher modules |

## Mitigation

- Exclude unused archetype HTL paths in `ui.apps/pom.xml`; remove CIF commerce content.
- Document publish as ticket runtime; seed data on publish (`:4503`).
- Use Sling Form Auth (`j_security_check`) + `LoginSelectorHandler` OSGi on publish.
- Load clientlibs synchronously; add `SupportUi.onReady()`.
- Provide manual user-creation fallback in `database/setup-notes.md`.
- Keep dispatcher modules in repo; document Windows symlink workaround in debugging notes.
