# Final AI Usage Summary

| Phase | AI contribution | Human validation |
|---|---|---|
| Planning | Requirements analysis, API contract, implementation plan, acceptance criteria | Reviewed against assignment brief; chose AEMaaCS option |
| Design | JCR schema, service layering, publish auth architecture, logout design | Rejected Granite/system login on publish; chose j_security_check |
| Implementation | Java services, servlets, HTL, OSGi, clientlibs, repoinit | Maven compile + unit tests after each change |
| Testing | Integration + unit test authoring, test-strategy.md | 11 transition tests + expanded unit suite verified |
| Debugging | HTL excludes, CUG/login fixes, async JS, author/publish data explanation | Manual SDK reproduction on :4502 and :4503 |
| Documentation | All assessment markdown files, ai-prompts with accept/reject rationale | Accuracy review, candidate-info filled |
| Code review | Security traceability, template alignment | Manual review of auth and state machine |

**Primary tool:** Cursor Agent with AEM skills (`ensure-agents-md`, `create-component` conventions) and project rules.

**Risk controls:**
- No secrets in prompts or repository
- Business rules verified by automated state-machine integration tests
- Publish auth fixes validated with curl and browser, not only AI suggestions
- Rejected AI suggestions that conflicted with AEM Cloud Service constraints

**Prompt history:** See [ai-prompts/](ai-prompts/) grouped by activity (planning, design, implementation, testing, debugging, code-review, documentation).

**Reusable patterns:**
1. Provide stack context in every prompt (AEMaaCS, publish :4503, JCR /var)
2. Ask AI to run Maven tests after backend changes
3. Record accept/reject decisions in prompt history
4. Use debugging template: Problem → Investigate → AI help → Validate → Fix
