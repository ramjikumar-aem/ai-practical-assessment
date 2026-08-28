# Tool Workflow

## Primary AI tool

Cursor (Agent mode) with installed AEM skills.

## Project context provided to AI

- Assignment brief and required repository structure
- Existing planning artifacts (`requirements-analysis.md`, `api-contract.md`, etc.)
- AEM skills configuration (`.aem-skills-config.yaml`)
- `AGENTS.md` after archetype bootstrap

## Requirement analysis

AI used to interpret AEM-specific persistence constraints (`database/` vs `ui.content`) and map entities to JCR + AEM User Management.

## Planning and design

Implementation plan produced in Cursor Plan mode; design decisions captured in `design-notes.md` and `data-model.md`.

## Code generation

AI generated:

- OSGi services, repositories, servlets
- HTL components and clientlibs
- Integration test suite
- OSGi repoinit and servlet path configuration

## Validation of AI-generated code

- `mvn clean install` on `core` module
- Mandatory integration tests in `tests/` module (11 state-machine scenarios)
- Manual review of API contract alignment

## Testing with AI

AI authored `TicketStatusTransitionIntegrationTest` and unit tests for transition map and validators.

## Debugging with AI

Compilation fixes (Java 11 compatibility, servlet PATCH handling, test classpath).

## Code review with AI

Self-review against `acceptance-criteria.md` and assignment state-machine rules.

## Information not shared with AI

- Production credentials
- Adobe Software Distribution account details
- Customer-specific environment secrets

## Reuse in real projects

Same workflow: bootstrap AEM archetype, lock design docs, use skills for components/servlets, enforce integration tests for business rules, keep AI prompt history for auditability.
