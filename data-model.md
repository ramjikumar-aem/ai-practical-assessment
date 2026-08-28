# Data Model

## User

The assignment specifies that User comes from AEM User Management.

| Field | Type | Notes |
|---|---|---|
| id | string | AEM user identifier/reference |
| name | string | User display/name value |
| email | string | User email |
| role | string | Role as exposed by the selected user-management design |

## Ticket

| Field | Type | Required | Notes |
|---|---|---:|---|
| id | string | Yes | Unique identifier |
| title | string | Yes | Backend validated |
| description | string | Yes | Backend validated |
| priority | enum/string | Yes | Allowed values must be documented |
| status | enum | Yes | Lifecycle state |
| assignedTo | user reference | Yes/decision | Assignment policy must be documented |
| createdBy | user reference | Yes | Creator |
| createdAt | timestamp | Yes | Creation time |
| updatedAt | timestamp | Yes | Last mutation time |

Status values: `OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`, `CANCELLED`.

## Comment

| Field | Type | Required | Notes |
|---|---|---:|---|
| id | string | Yes | Unique identifier |
| ticketId | ticket reference | Yes | Parent ticket |
| message | string | Yes | Backend validated |
| createdBy | user reference | Yes | Creator |
| createdAt | timestamp | Yes | Creation time |

## Relationships

- One Ticket is created by one User.
- One Ticket may be assigned to one User.
- One Ticket has zero or more Comments.
- Each Comment belongs to exactly one Ticket.
- User data is integrated from AEM User Management rather than duplicated as an unrelated entity.

## Integrity Rules

- `ticketId` for a comment must resolve to an existing ticket.
- User references must follow the selected AEM integration policy.
- Ticket status changes must satisfy the transition graph.
- Timestamp values are server controlled.
- Identifiers are immutable after creation.
