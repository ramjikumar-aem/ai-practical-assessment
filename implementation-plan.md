# Implementation Plan

## Phase 1 — Bootstrap
1. Create the AEM project using the latest AEMaaCS SDK-compatible project structure.
2. Establish modules for backend logic and UI/content as appropriate to the generated AEM architecture.
3. Add repository directories required by the brief, including `tests/`, `database/`, `ai-prompts/`, and `tool-specific/cursor-workflow/`.
4. Define local build and test commands.

## Phase 2 — Domain Model and Persistence
1. Define Ticket and Comment domain representations.
2. Integrate with the assignment's AEM User Management model rather than duplicating an independent user store.
3. Implement a persistence adapter suitable for AEM and capable of surviving restart.
4. Provide sample/seed data and setup notes.
5. Verify restart persistence.

## Phase 3 — Backend
1. Implement ticket creation.
2. Implement list, detail, keyword search, and status filtering.
3. Implement field updates.
4. Implement a dedicated status-transition service with a single authoritative transition map.
5. Implement comments.
6. Add validation and consistent error mapping.

## Phase 4 — API Contract
1. Freeze request/response shapes.
2. Define status codes and error payloads.
3. Validate identifiers, required fields, enum values, and transition legality.
4. Keep transition validation in backend service logic, not only UI controls.

## Phase 5 — Frontend
1. Ticket list page with keyword search and status filter.
2. Ticket creation flow.
3. Ticket detail view.
4. Editable ticket fields.
5. Lifecycle controls derived from current status.
6. Comment composer and history.
7. Loading, empty, validation, and error states.

## Phase 6 — Testing
1. Unit-test pure transition logic where practical.
2. Add mandatory integration tests for every valid and representative invalid transition.
3. Add API validation and persistence behavior tests.
4. Perform end-to-end/manual UI verification.

## Phase 7 — Documentation and Review
1. Complete README setup instructions.
2. Record prompt history by category.
3. Add test results, debugging notes, code-review notes, fixes, PR description, reflection, and AI usage summary.
4. Perform final build, test, restart-persistence, and acceptance-criteria pass.
