# Requirements Analysis

## 1. Source Scope

This document analyzes the assignment brief for a **Support Ticket Management System** built on **AEM**, using the **latest AEM as a Cloud Service SDK**. The solution must include a frontend, backend API, persistence, setup/migration or equivalent repository initialization, sample data, validation, error handling, search/filtering, integration testing, documentation, AI workflow artifacts, and supporting engineering notes.

## 2. Functional Requirements

### Users
The system uses AEM User Management users with:
- `id`
- `name`
- `email`
- `role`

The ticket domain references users through `assignedTo` and `createdBy`.

### Tickets
A ticket must contain:
- `id`
- `title`
- `description`
- `priority`
- `status`
- `assignedTo`
- `createdBy`
- `createdAt`
- `updatedAt`

Required capabilities:
1. Create a ticket.
2. List tickets.
3. View ticket detail.
4. Update title, description, priority, and assignee.
5. Change status only through the defined state machine.
6. Add comments.
7. Search by keyword.
8. Filter by status.

### Comments
A comment contains:
- `id`
- `ticketId`
- `message`
- `createdBy`
- `createdAt`

Comments are always associated with an existing ticket.

## 3. Lifecycle Rules

Allowed transitions:

| Current status | Allowed next status |
|---|---|
| Open | In Progress, Cancelled |
| In Progress | Resolved, Cancelled |
| Resolved | Closed |
| Closed | None |
| Cancelled | None |

Any other transition is invalid. The backend is the authoritative enforcement point; the frontend must present the failure clearly.

## 4. Non-Functional and Engineering Requirements

- Data must persist across application restart.
- Backend must validate required fields.
- Invalid backend input must be rejected.
- UI must expose meaningful error states.
- At least one working search/filter capability is required; keyword search and status filtering are specified.
- Mandatory test tier: integration tests proving valid transitions succeed and invalid transitions are rejected.
- Repository documentation and AI workflow artifacts are required.
- AEM implementation should target the latest available AEMaaCS SDK.

## 5. AEM Interpretation

The assignment says `database/` is not relevant for AEM push content in `ui.content`. Therefore, the implementation should keep persistence/setup concerns separate from AEM content-package deployment. A concrete persistence mechanism is an implementation decision and must preserve data across restart.

## 6. Constraints and Open Decisions

The brief does not prescribe:
- exact persistence technology,
- REST endpoint paths,
- authentication/authorization model,
- allowed priority values,
- ticket creation defaults,
- pagination/sorting,
- user-management integration mechanism.

These must be documented as explicit design decisions rather than presented as source requirements.

## 7. Requirement Traceability

| Requirement | Verification |
|---|---|
| Create/list/detail/update ticket | API and UI tests/manual flow |
| Enforced status transitions | Mandatory integration tests |
| Add comments | API behavior test |
| Search/filter | Functional UI/API test |
| Persistence across restart | Restart/manual integration verification |
| Backend validation | Negative API tests |
| Meaningful UI errors | UI/manual verification |
| Setup/sample data | README and setup verification |
