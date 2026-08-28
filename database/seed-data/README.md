# Seed sample tickets (run after AEM SDK is up)

Use the REST API as an authenticated AEM user (for example `support-agent`).

## Linux / macOS / Git Bash

```bash
curl -u support-agent:support123 -H "Content-Type: application/json" \
  -X POST http://localhost:4502/bin/api/support/tickets.json \
  -d '{"title":"Login failure","description":"User cannot sign in","priority":"HIGH","assignedTo":"support-manager"}'
```

## Windows CMD

CMD does not treat single quotes as string delimiters. Escape double quotes inside `-d`:

```cmd
curl -u support-agent:support123 -H "Content-Type: application/json" -X POST http://localhost:4502/bin/api/support/tickets.json -d "{\"title\":\"Login failure\",\"description\":\"User cannot sign in\",\"priority\":\"HIGH\",\"assignedTo\":\"support-manager\"}"
```

## Windows PowerShell

```powershell
curl.exe -u support-agent:support123 -H "Content-Type: application/json" -X POST http://localhost:4502/bin/api/support/tickets.json -d "{\"title\":\"Login failure\",\"description\":\"User cannot sign in\",\"priority\":\"HIGH\",\"assignedTo\":\"support-manager\"}"
```

Optional second ticket (Git Bash / Linux):

```bash
curl -u support-manager:support123 -H "Content-Type: application/json" \
  -X POST http://localhost:4502/bin/api/support/tickets.json \
  -d '{"title":"Password reset email missing","description":"Reset email not received","priority":"MEDIUM","assignedTo":"support-agent"}'
```

Seed users are provisioned by repoinit when supported, or manually in AEM Security (`/useradmin`):

- `support-agent` / `support123` (group: `support-agents`, role: agent)
- `support-manager` / `support123` (group: `support-managers`, role: manager)

Verify users are visible to the API:

```cmd
curl -u support-agent:support123 http://localhost:4502/bin/api/support/users
```

If `support-manager` is missing, create the user and add to `support-managers`, then redeploy `ui.config` if repoinit did not run.
