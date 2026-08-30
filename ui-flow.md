# UI Flow

## 0. Login (publish)

Unauthenticated users hitting any ticket page are redirected to `/content/support-tickets/login.html?resource=<requested-page>`.

Flow:

`Protected page` → server redirect to `Login` (CUG / `sling:authRequireLogin` / publish filter) → submit credentials to `/content/support-tickets/login/j_security_check` → redirect back to requested page

If a protected page still renders without a session, API calls return `401` and the UI redirects to login as a fallback.

The login page uses the **Support Login** component (`support/components/login`) in the **Support Tickets - Content** group. Authors can configure:

- Heading
- Subtitle
- Default redirect path (used when no `?resource=` query parameter is present)

Outcomes:
- success → session cookie set; user can access list/create/detail;
- failure → login page redisplays with Granite error handling.

## 0.1 Logout (publish)

Authenticated users see a **Sign Out** link in the user bar on list, create, and detail pages.

Flow:

`Ticket page` → click **Sign Out** → `GET /content/support-tickets.logout.html?resource=/content/support-tickets/login.html` → `SupportLogoutServlet` calls `Authenticator.logout()` → redirect to login page → session cookie cleared

Outcomes:
- success → user lands on `/content/support-tickets/login.html` as anonymous;
- revisiting protected pages → login redirect required again.

Do not use `/system/sling/logout.html` on publish (same restrictions as `/system/sling/login`).

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
