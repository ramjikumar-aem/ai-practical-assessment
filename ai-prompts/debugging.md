# Debugging prompts

- Fix Maven compilation errors (PATCH servlet, imports)
- Fix integration test NoClassDefFoundError for Core Components mock plugin
- Document Windows dispatcher symlink issue
- Fix `ui.apps` HTL compile errors for archetype commerce/forms components missing compile classpath:
  - `com.adobe.cq.commerce.core.components.models.header`
  - `com.adobe.cq.forms.core.components.models.form`
  - Exclude `components/commerce` and `components/adaptiveForm` from `htl-maven-plugin` Java generation in `ui.apps/pom.xml`
- Fix vault filter errors for archetype forms/commerce content in `ui.apps` and `ui.content`
- Fix AEM analyser errors: remove `SlingServletResolver` `sling.servlet.paths` OSGi config; fix repoinit user password syntax
- User request: `@terminals/2.txt:232-241 check errors and fix, Also maintain prompts in respective files`
