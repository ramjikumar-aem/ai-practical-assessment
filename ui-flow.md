# UI Flow

## 1. Ticket List

Entry flow:

`Ticket List` → search keyword → optional status filter → filtered results

Actions:
- Create Ticket
- Open Ticket Detail

States:
- loading;
- populated;
- empty;
- no search results;
- request error.

## 2. Create Ticket

`Ticket List` → `Create Ticket` → enter required fields → submit

Outcomes:
- success → redirect to ticket detail with `?id={ticketId}`; detail page initializes via `SupportUi.onReady()` and fetches ticket data;
- validation failure → inline field messages;
- request failure → meaningful non-field error.

## 3. Ticket Detail

`Ticket List` → select ticket → `Ticket Detail`

The detail screen presents:
- current ticket fields;
- current status;
- available lifecycle actions;
- comments;
- update action;
- add-comment action.

## 4. Field Update

`Ticket Detail` → edit supported fields → submit → backend validation

Outcomes:
- success → refreshed data;
- validation failure → retain form and show field errors;
- request failure → show meaningful error.

## 5. Status Change

`Ticket Detail` → select target transition → backend request

Valid flow:
`Open → In Progress → Resolved → Closed`

Alternative cancellation flows:
`Open → Cancelled`
`In Progress → Cancelled`

If the backend rejects a transition, the UI displays the returned failure and retains the persisted status.

## 6. Comments

`Ticket Detail` → enter message → submit → comment appended/refreshed

Invalid input and nonexistent-ticket errors are shown clearly.

## Accessibility/Interaction Note

Controls should expose clear labels, disabled/loading states, keyboard-usable interaction, and error feedback associated with the affected form or action.
