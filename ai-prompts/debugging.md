# Debugging Prompts

## Prompt 1: Maven build failures

**Prompt (summary):**  
Fix Maven compilation errors (PATCH servlet, imports). Fix integration test NoClassDefFoundError for Core Components mock plugin. Document Windows dispatcher symlink issue.

**AI response summary:**  
PATCH via service() override; plain AemContext without Core Components plugin; dispatcher exclusion documented.

**Accepted:**  
Both code fixes and debugging notes entry.

**Changed:**  
Added HTL exclude fixes when ui.apps failed on commerce/forms models.

**Rejected:**  
Force-installing missing commerce JARs — unnecessary for ticket app.

---

## Prompt 2: HTL and vault filter errors

**Prompt (summary):**  
Fix ui.apps HTL compile errors for archetype commerce/forms components. Fix vault filter errors for forms/commerce content.

**AI response summary:**  
Exclude adaptiveForm/commerce from htl-maven-plugin; extend filter.xml; remove CIF content from ui.content.

**Accepted:**  
Full archetype cleanup.

**Changed:**  
None.

**Rejected:**  
Keeping archetype commerce pages in ui.content.

---

## Prompt 3: Detail page empty after create

**Prompt (summary):**  
Detail page not auto-populating after create; works on refresh.

**AI response summary:**  
Async clientlib caused missed DOMContentLoaded. Fix: sync load + SupportUi.onReady().

**Accepted:**  
Root cause and fix.

**Changed:**  
None.

**Rejected:**  
Adding setTimeout retry hacks — onReady is cleaner.

---

## Prompt 4: Publish authentication failures

**Prompt (summary):**  
Publish shows "Authentication required" instead of login redirect. Login fails with Granite 500, system/sling 403, invalid credentials with custom servlet.

**AI response summary:**  
Layered fix: CUG, redirect filter, j_security_check, FormAuthenticationHandler, LoginSelectorHandler, remove custom login servlet.

**Accepted:**  
Final j_security_check approach and OSGi configs.

**Changed:**  
Added GraniteLoginRedirectFilter after login URL still pointed to /libs/granite.

**Rejected:**  
Each intermediate fix (granite login, system/sling, custom servlet) — validated as broken on publish.

---

## Prompt 5: Logout not working

**Prompt (summary):**  
Login works but unable to logout after login. Implement logout functionality.

**AI response summary:**  
SupportLogoutServlet + user-bar + Authenticator.logout() plan.

**Accepted:**  
Full implementation plan and execution.

**Changed:**  
None.

**Rejected:**  
/system/sling/logout.html.

---

## Prompt 6: Author vs publish data mismatch

**Prompt (summary):**  
Created ticket details differ on author and publish. How to centralize data?

**AI response summary:**  
Explained separate /var repositories. Options: publish-only runtime, external DB, replication (not recommended).

**Accepted:**  
Documentation of publish as ticket runtime.

**Changed:**  
Added assumption to requirements-analysis.md.

**Rejected:**  
Implementing external DB in current scope — documented as future improvement.
