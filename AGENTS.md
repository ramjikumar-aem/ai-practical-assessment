# Support Tickets

This is an AEM as a Cloud Service project using the Java stack.

Assignment implementation lives in `src/support` (AEM Maven modules) with additional integration tests in `tests/`.

## Modules

- `src/support/core`: OSGi bundle with ticket domain services, JCR repositories, and `/bin/api/support` servlets.
- `src/support/ui.apps`: HTL components (`ticket-list`, `ticket-form`, `ticket-detail`) and clientlibs.
- `src/support/ui.config`: OSGi configs including repoinit for `/var/support-tickets`.
- `src/support/ui.content`: Authorable pages only (`/content/support-tickets`; archetype CIF commerce content removed).
- `src/support/all`: Aggregated deployment package.
- `tests`: Mandatory state-machine integration tests.

## Build

From `src/support`:

```bash
mvn clean install
```

Deploy to local AEM SDK author:

```bash
mvn clean install -PautoInstallSinglePackage
```

Run integration tests:

```bash
mvn clean verify -pl ../../tests
```

## Application URLs

- Ticket list: `http://localhost:4502/content/support-tickets.html`
- Create ticket: `http://localhost:4502/content/support-tickets/create.html`
- Ticket detail: `http://localhost:4502/content/support-tickets/detail.html?id={ticketId}`

## API

Base path: `/bin/api/support`

See `api-contract.md` for request/response shapes.

## Important resources

- [AEM as a Cloud Service SDK](https://experienceleague.adobe.com/en/docs/experience-manager-cloud-service/content/implementing/developing/aem-as-a-cloud-service-sdk)
- [AEM Project Structure](https://experienceleague.adobe.com/en/docs/experience-manager-cloud-service/content/implementing/developing/aem-project-content-package-structure)
- [WCM.io AEM Mocks](https://wcm.io/testing/aem-mock/)
