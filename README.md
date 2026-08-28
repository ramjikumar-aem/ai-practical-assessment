# Support Ticket Management System (AEMaaCS)

AEM as a Cloud Service implementation of an internal support ticket application with HTL UI, JSON API servlets, JCR persistence, and mandatory state-machine integration tests.

## Prerequisites

- JDK 11+
- Maven 3.3.9+
- AEM as a Cloud Service SDK (author instance on port 4502)

## Project layout

| Path | Purpose |
|---|---|
| `src/support/` | AEM Maven modules (`core`, `ui.apps`, `ui.content`, `ui.config`, `all`) |
| `tests/` | Mandatory integration tests for ticket status transitions |
| `database/` | JCR schema notes, seed instructions, setup notes |
| `ai-prompts/` | AI prompt history by workflow phase |

`ui.content` ships ticket pages under `/content/support-tickets` only. Archetype CIF commerce content (`/content/support`, `/var/commerce`) was removed.

## Build

```bash
cd src/support
mvn clean install
```

Run integration tests:

```bash
mvn clean install -pl ../../tests -am
```

Deploy to local AEM author:

```bash
mvn clean install -PautoInstallSinglePackage
```

## Demo URLs

After deployment:

- List: http://localhost:4502/content/support-tickets.html
- Create: http://localhost:4502/content/support-tickets/create.html
- Detail: http://localhost:4502/content/support-tickets/detail.html?id={ticketId}

## API

Base path: `/bin/api/support`

See [api-contract.md](api-contract.md).

## Seed data

See [database/setup-notes.md](database/setup-notes.md) and [database/seed-data/README.md](database/seed-data/README.md).

Sample users (repoinit):

- `support-agent` / `support123`
- `support-manager` / `support123`

## Persistence verification

1. Create ticket via UI or API.
2. Restart AEM SDK.
3. Confirm ticket still listed.

## Test evidence

Latest run recorded in [test-results.md](test-results.md).
