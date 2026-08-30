# Planning Prompts

## Prompt 1: Bootstrap project from assignment brief

**Prompt (summary):**  
Create AEMaaCS Support Ticket Management System from the assignment brief. Use latest AEM SDK archetype 57, HTL frontend, JCR persistence. Include `src/`, `tests/`, `database/`, `ai-prompts/`, assessment documentation structure.

**AI response summary:**  
Proposed Maven multi-module layout, requirements analysis, API contract draft, implementation phases, and repo folder structure matching the assessment template.

**Accepted:**  
- Archetype 57 with appId `support`  
- JCR under `/var/support-tickets`  
- Mandatory integration tests for state machine  
- Assessment doc file set

**Changed:**  
- Removed CIF commerce content after archetype generation  
- Adjusted API base path to `/bin/api/support` (AEM servlet convention)

**Rejected:**  
- Storing tickets in `ui.content` — brief says database folder is not AEM content; `/var` is correct for transactional data.

---

## Prompt 2: Requirements and acceptance criteria structure

**Prompt (summary):**  
Structure requirements-analysis.md and acceptance-criteria.md per assessment template with functional/non-functional requirements, assumptions, edge cases, and checkbox acceptance criteria.

**AI response summary:**  
Drafted traceability table mapping requirements to verification methods; checkbox format for acceptance criteria grouped by Core, Validation, Error Handling, Testing, Documentation.

**Accepted:**  
Full template structure, edge cases for auth and author/publish isolation.

**Changed:**  
Marked completed items `[x]` based on implemented features.

**Rejected:**  
None — template applied as specified.

---

## Prompt 3: Implementation plan with AI usage and risks

**Prompt (summary):**  
Add implementation plan with milestones, AI usage plan, risks, and mitigation for AEM assessment submission.

**AI response summary:**  
Phased plan M1–M6, risk table (archetype bloat, publish auth, async JS, Windows dispatcher), mitigation strategies.

**Accepted:**  
Task breakdown table, milestone dates, risk/mitigation sections.

**Changed:**  
Added publish auth and logout as explicit tasks after scope expansion.

**Rejected:**  
External database in v1 — out of assessment scope.
