# Source code

AEM Maven multi-module project lives in [`support/`](support/).

| Module | Description |
|---|---|
| `support/core` | OSGi bundle (API, services, JCR repositories) |
| `support/ui.apps` | HTL components and clientlibs |
| `support/ui.content` | Authorable pages |
| `support/ui.config` | OSGi configuration and repoinit |
| `support/all` | Deployment package |

Build from `support/`:

```bash
mvn clean install
```
