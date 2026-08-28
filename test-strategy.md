# Test Strategy

## 1. Objective

Verify functional correctness, lifecycle enforcement, persistence, validation, API behavior, and meaningful user-facing failure handling.

## 2. Test Pyramid

### Mandatory: Integration Tests
The required integration suite focuses on the status state machine.

Valid cases:
- Open → In Progress
- Open → Cancelled
- In Progress → Resolved
- In Progress → Cancelled
- Resolved → Closed

Invalid cases include at minimum:
- Open → Resolved
- Open → Closed
- In Progress → Open
- Resolved → In Progress
- Closed → any state
- Cancelled → any state

For each invalid case, assert:
1. backend rejection;
2. appropriate error response;
3. persisted status remains unchanged.

### Unit Tests
Where the transition rule is implemented as a pure service/map, test:
- allowed target lookup;
- terminal states;
- null/unknown status handling;
- validation helpers.

### API/Integration Coverage
Test:
- create valid ticket;
- required-field rejection;
- list;
- detail and missing detail;
- update supported fields;
- keyword search;
- status filter;
- comment creation;
- comment validation;
- persistence across restart or equivalent lifecycle verification.

### UI Verification
Verify:
- loading;
- empty/no-results;
- backend validation messages;
- invalid-transition message;
- generic request failure;
- successful search/filter/update/comment flows.

## 3. Test Data

Use deterministic fixture users and tickets. Each lifecycle test should begin from a known persisted state and avoid cross-test coupling.

## 4. Exit Criteria

Before completion:
- all mandatory transition integration tests pass;
- no invalid transition mutates state;
- core acceptance criteria are verified;
- build/test commands are documented;
- test results are recorded in `test-results.md`.

## 5. Traceability

| Area | Primary Evidence |
|---|---|
| State machine | Integration test suite |
| Backend validation | Negative API tests |
| Search/filter | Functional test |
| Persistence | Restart/lifecycle verification |
| UI errors | UI test or documented manual verification |
| Regression | Full automated suite |
