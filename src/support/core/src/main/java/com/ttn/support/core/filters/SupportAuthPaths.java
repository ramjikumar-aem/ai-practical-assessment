package com.ttn.support.core.filters;

public final class SupportAuthPaths {

    static final String CONTENT_ROOT = "/content/support-tickets";
    static final String LOGIN_CONTENT_PATH = CONTENT_ROOT + "/login";
    public static final String DEFAULT_LOGIN_PAGE = LOGIN_CONTENT_PATH + ".html";
    public static final String LOGOUT_PAGE_PATH = CONTENT_ROOT + ".logout.html";

    private SupportAuthPaths() {
    }

    static boolean isProtectedContentPath(String resourcePath) {
        if (resourcePath == null || resourcePath.isBlank()) {
            return false;
        }
        if (!resourcePath.equals(CONTENT_ROOT) && !resourcePath.startsWith(CONTENT_ROOT + "/")) {
            return false;
        }
        return !resourcePath.equals(LOGIN_CONTENT_PATH);
    }

    static String buildLoginRedirectUrl(String resourcePath, String extension, String queryString) {
        StringBuilder resource = new StringBuilder(resourcePath);
        if (extension != null && !extension.isBlank()) {
            resource.append(".").append(extension);
        }
        if (queryString != null && !queryString.isBlank()) {
            resource.append("?").append(queryString);
        }
        return DEFAULT_LOGIN_PAGE + "?resource="
                + java.net.URLEncoder.encode(resource.toString(), java.nio.charset.StandardCharsets.UTF_8);
    }

    public static String buildLogoutUrl() {
        return LOGOUT_PAGE_PATH + "?resource="
                + java.net.URLEncoder.encode(DEFAULT_LOGIN_PAGE, java.nio.charset.StandardCharsets.UTF_8);
    }
}
