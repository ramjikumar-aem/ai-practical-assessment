# Acceptance Criteria

## Ticket Creation
- Given valid required ticket data, when a ticket is submitted, then it is persisted and returned with an identifier and timestamps.
- Given required data is missing, when creation is attempted, then the backend rejects the request with a meaningful validation error.
- Given an invalid assignee or creator reference is supplied, then the request is rejected according to the chosen user-integration design.

## Ticket Listing, Search, and Filter
- A user can retrieve persisted tickets.
- A keyword search matches the documented searchable ticket fields.
- A status filter returns only tickets with the requested status.
- Search and status filtering can be combined if both query parameters are supplied.
- Invalid filter values are rejected or handled according to the documented API contract.

## Ticket Detail and Update
- An existing ticket can be retrieved by ID.
- A missing ticket produces a meaningful not-found response.
- Title, description, priority, and assignee can be updated.
- Invalid update input is rejected by the backend.
- A successful update changes `updatedAt`.

## Status State Machine
- `Open -> In Progress` succeeds.
- `Open -> Cancelled` succeeds.
- `In Progress -> Resolved` succeeds.
- `In Progress -> Cancelled` succeeds.
- `Resolved -> Closed` succeeds.
- Every transition not listed above is rejected by the backend.
- Rejected transitions do not change the persisted status.
- The frontend displays a clear failure message for a rejected transition.

## Comments
- A comment can be added to an existing ticket.
- A comment includes ticket association, message, creator, and creation timestamp.
- Missing required comment input is rejected.
- Adding a comment to a nonexistent ticket returns a meaningful error.

## Persistence
- Created tickets and comments remain available after an application restart.
- Seed/sample data can be loaded using the documented setup process.

## UI
- Users can create and browse tickets.
- Users can open ticket detail.
- Users can update supported ticket fields.
- Users can initiate only supported lifecycle actions or receive clear backend-driven feedback.
- Users can add comments.
- Search and status filtering are usable.
- Loading, empty, validation, not-found, and request-failure states are meaningful where applicable.

## Mandatory Integration Test Evidence
The test suite must include integration tests proving:
1. all valid transitions succeed;
2. invalid transitions are rejected;
3. rejected transitions do not mutate persisted state.

A submission is not complete unless these tests are executable and documented.
