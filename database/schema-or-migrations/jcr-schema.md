# Support Ticket JCR Schema

Transactional ticket and comment data is stored under `/var/support-tickets` and is **not** deployed through `ui.content`.

## Ticket node (`/var/support-tickets/tickets/{id}`)

| Property | Type | Notes |
|---|---|---|
| title | String | Required |
| description | String | Required |
| priority | String | `LOW`, `MEDIUM`, `HIGH` |
| status | String | Lifecycle state |
| assignedTo | String | AEM user id |
| createdBy | String | AEM user id |
| createdAt | String | ISO-8601 instant |
| updatedAt | String | ISO-8601 instant |

## Comment node (`/var/support-tickets/comments/{id}`)

| Property | Type | Notes |
|---|---|---|
| ticketId | String | Parent ticket id |
| message | String | Required |
| createdBy | String | AEM user id |
| createdAt | String | ISO-8601 instant |

## Repository initialization

Structure and sample users are created via repoinit in:

`src/support/ui.config/.../org.apache.sling.jcr.repoinit.RepositoryInitializer~support.cfg.json`
