# Testing Prompts

## Prompt 1: Mandatory integration tests

**Prompt (summary):**  
Mandatory integration tests for all valid and representative invalid status transitions. Unit tests for StatusTransitionService and TicketValidator. Record results in test-results.md.

**AI response summary:**  
`TicketStatusTransitionIntegrationTest` with 11 parameterized cases. Unit tests for transition service and validator. `SupportTestContext` for AEM Mock setup.

**Accepted:**  
Full transition matrix coverage, assert 409 + unchanged status on invalid cases.

**Changed:**  
Used plain `AemContextBuilder` instead of `AppAemContext` after Core Components classpath errors.

**Rejected:**  
Skipping invalid transition persistence check — mandatory per assignment brief.

---

## Prompt 2: Servlet and auth unit tests

**Prompt (summary):**  
Add unit tests for API auth rejection, route parser, logout servlet, user bar model.

**AI response summary:**  
`AuthSupportTest`, `SupportTicketsServletTest`, `SupportLogoutServletTest`, `UserBarModelTest`, `SupportAuthPathsTest`.

**Accepted:**  
All test classes.

**Changed:**  
Spy `ResourceResolver` for mock user ID in servlet/model tests.

**Rejected:**  
None.

---

## Prompt 3: Test strategy documentation

**Prompt (summary):**  
Structure test-strategy.md per assessment template: scope, unit/component/API/edge case sections, tests not covered.

**AI response summary:**  
Full test strategy with traceability table and explicit gaps (E2E, dispatcher, author/publish sync).

**Accepted:**  
Template structure and honest "not covered" section.

**Changed:**  
Updated test counts after logout tests added.

**Rejected:**  
Claiming full E2E automation exists — manual verification only.
