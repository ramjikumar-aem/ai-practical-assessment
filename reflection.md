# Reflection

Building on AEM reinforced keeping domain rules (status transitions) in OSGi services rather than UI-only checks. JCR under `/var` is a practical persistence choice for a scratch AEM app without external databases.

The mandatory integration test tier caught classpath/setup issues early (Core Components mock plugin vs plain AEM Mock).

If revisiting, would add Oak Query for search and servlet-level integration tests with mocked `SlingHttpServletRequest` for full API contract verification.
