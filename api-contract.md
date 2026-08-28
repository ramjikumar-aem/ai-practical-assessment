# API Contract

> Proposed contract: the assignment specifies capabilities but not endpoint paths. Paths and payloads below are implementation decisions.

## Conventions
Base path: `/api/support`

Error payload:
```json
{
  "code": "VALIDATION_ERROR",
  "message": "title is required",
  "fields": {
    "title": "required"
  }
}
```

## Tickets

### Create
`POST /tickets.json`

Request:
```json
{
  "title": "Login failure",
  "description": "User cannot sign in",
  "priority": "HIGH",
  "assignedTo": "user-id"
}
```

Response: `201 Created` with Ticket.

The implementation must document how `createdBy` is resolved from AEM user context.

### List/Search/Filter
`GET /tickets.json?q=login&status=OPEN`

Response: `200 OK`
```json
{
  "items": [
    {
      "id": "ticket-id",
      "title": "Login failure",
      "description": "User cannot sign in",
      "priority": "HIGH",
      "status": "OPEN",
      "assignedTo": "user-id",
      "createdBy": "user-id",
      "createdAt": "2026-08-20T00:00:00Z",
      "updatedAt": "2026-08-20T00:00:00Z"
    }
  ]
}
```

### Detail
`GET /tickets.{ticketId}.json` → `200`, or `404`.

### Update Fields
`PATCH /tickets.{ticketId}.json`

Supported mutable fields:
```json
{
  "title": "Updated title",
  "description": "Updated description",
  "priority": "MEDIUM",
  "assignedTo": "user-id"
}
```

### Transition Status
`POST /tickets.{ticketId}.status.json`

```json
{
  "status": "IN_PROGRESS"
}
```

Success: `200 OK`.

Illegal transition: `409 Conflict` with:
```json
{
  "code": "INVALID_STATUS_TRANSITION",
  "message": "OPEN cannot transition to RESOLVED"
}
```

### Add Comment
`POST /tickets.{ticketId}.comments.json`

```json
{
  "message": "Investigating the issue"
}
```

Creator resolution should follow the same documented AEM user-context policy.

### List Comments
`GET /tickets.{ticketId}.comments.json` → `200 OK`.

## Validation and Status Codes

- `201` resource created
- `200` successful read/update/transition
- `400` malformed request
- `404` resource not found
- `409` illegal state transition or documented conflict
- `422` semantically invalid input, if adopted consistently
- `500` unexpected failure

The final implementation must use one consistent policy rather than mixing equivalent validation responses arbitrarily.
