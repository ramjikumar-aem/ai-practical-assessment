# Documentation Prompts

## Prompt 1: Assessment artifact set

**Prompt (summary):**  
Complete assignment artifact set: README, tool-workflow, reflection, PR description, candidate-info, all required markdown files.

**AI response summary:**  
Generated/updated root-level docs, linked cross-references, candidate-info template.

**Accepted:**  
File structure matching assessment brief.

**Changed:**  
Expanded reflection and pr-description after auth/logout features added.

**Rejected:**  
Duplicating content across every file — each file has distinct purpose per template.

---

## Prompt 2: Database and setup notes

**Prompt (summary):**  
database/setup-notes.md for JCR persistence, seed users, publish verification checklist.

**AI response summary:**  
Repoinit steps, user table, curl verification commands, publish/dispatcher checklist.

**Accepted:**  
Setup notes with Windows CMD and PowerShell curl examples.

**Changed:**  
Added publish login j_security_check and author/publish data isolation notes.

**Rejected:**  
SQL migration scripts — not applicable for JCR persistence.

---

## Prompt 3: UI flow and design notes

**Prompt (summary):**  
Document UI flows for login, logout, ticket CRUD, and publish auth in ui-flow.md and design-notes.md.

**AI response summary:**  
Step-by-step flows, architecture diagram, auth/dispatcher sections.

**Accepted:**  
Login and logout flows, CUG section, dispatcher policy.

**Changed:**  
Reorganized design-notes to match template (Architecture Overview, Frontend/Backend/Database Design).

**Rejected:**  
Vanity URL paths (/content/support/) — project uses /content/support-tickets/*.html.

---

## Prompt 4: AGENTS.md for AEM project

**Prompt (summary):**  
AGENTS.md for AEM project guidance per ensure-agents-md skill.

**AI response summary:**  
Module map, build commands, deployment notes for support project.

**Accepted:**  
AGENTS.md and CLAUDE.md at repo and module level.

**Changed:**  
None.

**Rejected:**  
None.
