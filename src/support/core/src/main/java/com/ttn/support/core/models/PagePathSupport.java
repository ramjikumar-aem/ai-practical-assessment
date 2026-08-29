package com.ttn.support.core.models;

final class PagePathSupport {

    private PagePathSupport() {
    }

    static String toHtmlPath(String path, String defaultPath) {
        if (path == null || path.isBlank()) {
            return defaultPath;
        }
        return path.endsWith(".html") ? path : path + ".html";
    }
}
