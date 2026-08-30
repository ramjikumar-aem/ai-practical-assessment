# API Contract

Base path: `/bin/api/support`  
Content-Type: `application/json`  
Authentication: AEM session user (Basic auth or form-auth cookie). Anonymous → `401`.

## Error envelope

```json
{
  "code": "VALIDATION_ERROR",
  "message": "title is required",
  "fields": {
    "title": "required"
  }
}
```

---

## Endpoint: List / Create Tickets

**Method:** `GET` / `POST`  
**Path:** `/bin/api/support/tickets.json`  
**Purpose:** List tickets (with optional search/filter) or create a new ticket.

### Request (GET query params)

| Param | Type | Required | Notes |
|---|---|---|---|
| `q` | string | No | Keyword search in title and description |
| `status` | string | No | Filter: `OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`, `CANCELLED` |

### Request (POST body)

```json
{
  "title": "Login failure",
  "description": "User cannot sign in",
  "priority": "HIGH",
  "assignedTo": "support-manager"
}
```

### Response (GET `200`)

```json
{
  "items": [
    {
      "id": "967c4e7f-2620-4a67-a80a-c253f3d8aaf2",
      "title": "Login failure",
      "description": "User cannot sign in",
      "priority": "HIGH",
      "status": "OPEN",
      "assignedTo": "support-manager",
      "createdBy": "support-agent",
      "createdAt": "2026-08-28T10:00:00Z",
      "updatedAt": "2026-08-28T10:00:00Z"
    }
  ]
}
```

### Response (POST `201`)

Returns the created ticket object (same shape as list item).

### Validation Rules

- `title`, `description`, `priority`, `assignedTo` required on create.
- `priority` must be `LOW`, `MEDIUM`, or `HIGH`.
- `assignedTo` must reference an existing assignable AEM user.
- `createdBy` set from authenticated session user.
- New tickets start with status `OPEN`.

### Error Responses

| Status | Code | When |
|---|---|---|
| 400 | `VALIDATION_ERROR` | Missing/invalid fields |
| 401 | `UNAUTHORIZED` | Anonymous caller |

---

## Endpoint: Ticket Detail / Update

**Method:** `GET` / `PATCH`  
**Path:** `/bin/api/support/tickets.{ticketId}.json`  
**Purpose:** Retrieve or update a single ticket.

### Request (PATCH body)

```json
{
  "title": "Updated title",
  "description": "Updated description",
  "priority": "MEDIUM",
  "assignedTo": "support-agent"
}
```

All fields optional; only supplied fields are updated.

### Response (`200`)

Full ticket object.

### Validation Rules

- Ticket must exist.
- Same field rules as create for supplied fields.

### Error Responses

| Status | Code | When |
|---|---|---|
| 400 | `VALIDATION_ERROR` | Invalid update input |
| 401 | `UNAUTHORIZED` | Anonymous caller |
| 404 | `NOT_FOUND` | Ticket ID not found |

---

## Endpoint: Status Transition

**Method:** `POST`  
**Path:** `/bin/api/support/tickets.{ticketId}.status.json`  
**Purpose:** Change ticket status via the state machine.

### Request

```json
{
  "status": "IN_PROGRESS"
}
```

### Response (`200`)

Updated ticket object with new `status` and `updatedAt`.

### Validation Rules

- Transition must be allowed from current status (see design-notes state machine).
- Terminal states (`CLOSED`, `CANCELLED`) reject all transitions.

### Error Responses

| Status | Code | When |
|---|---|---|
| 401 | `UNAUTHORIZED` | Anonymous caller |
| 404 | `NOT_FOUND` | Ticket not found |
| 409 | `INVALID_STATUS_TRANSITION` | Illegal transition |

Example:
```json
{
  "code": "INVALID_STATUS_TRANSITION",
  "message": "OPEN cannot transition to RESOLVED"
}
```

---

## Endpoint: Comments

**Method:** `GET` / `POST`  
**Path:** `/bin/api/support/tickets.{ticketId}.comments.json`  
**Purpose:** List or add comments on a ticket.

### Request (POST body)

```json
{
  "message": "Investigating the issue"
}
```

### Response (GET `200`)

```json
{
  "items": [
    {
      "id": "comment-uuid",
      "ticketId": "967c4e7f-2620-4a67-a80a-c253f3d8aaf2",
      "message": "Investigating the issue",
      "createdBy": "support-agent",
      "createdAt": "2026-08-28T11:00:00Z"
    }
  ]
}
```

### Validation Rules

- `message` required and non-blank on create.
- Parent ticket must exist.
- `createdBy` from session user.

### Error Responses

| Status | Code | When |
|---|---|---|
| 400 | `VALIDATION_ERROR` | Empty message |
| 401 | `UNAUTHORIZED` | Anonymous caller |
| 404 | `NOT_FOUND` | Ticket not found |

---

## Endpoint: Assignable Users

**Method:** `GET`  
**Path:** `/bin/api/support/users.json`  
**Purpose:** List users available for ticket assignment.

### Response (`200`)

```json
{
  "items": [
    {
      "id": "support-agent",
      "name": "Support Agent",
      "email": "agent@example.com",
      "role": "agent"
    }
  ]
}
```

### Error Responses

| Status | Code | When |
|---|---|---|
| 401 | `UNAUTHORIZED` | Anonymous caller |

---

## Publish Login / Logout (not JSON API)

| Action | Method | Path |
|---|---|---|
| Login | POST | `/content/support-tickets/login/j_security_check` |
| Logout | GET | `/content/support-tickets.logout.html?resource=/content/support-tickets/login.html` |

Form fields for login: `j_username`, `j_password`, `_charset_`, `resource` (redirect after success).
