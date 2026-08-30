# Code Review Prompts

## Prompt 1: Acceptance criteria traceability

**Prompt (summary):**  
Verify acceptance criteria traceability. Check backend enforces state machine (not UI-only).

**AI response summary:**  
Confirmed StatusTransitionService is authoritative; UI shows backend errors. Mapped criteria to tests.

**Accepted:**  
Traceability table in requirements-analysis and acceptance-criteria.

**Changed:**  
Added checkbox status `[x]` for completed items.

**Rejected:**  
UI-only transition guards without backend check — violates assignment.

---

## Prompt 2: Security review

**Prompt (summary):**  
Review security: servlet registration, no secrets in repo, publish auth, open redirect on login resource param.

**AI response summary:**  
Flagged open redirect risk; recommended SupportLoginRedirect.sanitize(). Noted API auth and dispatcher cache deny.

**Accepted:**  
Sanitization utility, auth on all API endpoints, cache deny rules.

**Changed:**  
Removed SlingServletResolver paths config per analyser.

**Rejected:**  
Hardcoding credentials in repoinit for demo — use documented test passwords only, no secrets in repo.

---

## Prompt 3: Assessment documentation review

**Prompt (summary):**  
Check and update all assessment files per required template structure.

**AI response summary:**  
Restructured requirements-analysis, acceptance-criteria, implementation-plan, design-notes, api-contract, test-strategy, debugging-notes, code-review-notes, reflection, pr-description, ai-prompts.

**Accepted:**  
Full template alignment with implemented solution.

**Changed:**  
Updated api-contract paths from proposed `/api/support` to actual `/bin/api/support`.

**Rejected:**  
Leaving placeholder/stub content — filled with actual project details.
