# Pull Request Description

## Summary

Implements AEMaaCS Support Ticket Management System with JCR-backed persistence, REST-style servlets, HTL UI, and integration tests for the ticket lifecycle state machine.

## Changes

- Bootstrap AEM archetype 57 project in `src/support`
- Core bundle: models, repositories, services, `/bin/api/support` servlets
- UI: `ticket-list`, `ticket-form`, `ticket-detail` components + clientlib
- Pages: `/content/support-tickets`
- Repoinit for `/var/support-tickets` and sample users
- Integration tests: 11 state-machine scenarios in `tests/`

## Test plan

- [x] `mvn clean install -pl ../../tests -am`
- [ ] Deploy `all` package to AEM SDK
- [ ] Manual UI: create, list, search, update, transition, comment
- [ ] Restart AEM and verify ticket persistence
