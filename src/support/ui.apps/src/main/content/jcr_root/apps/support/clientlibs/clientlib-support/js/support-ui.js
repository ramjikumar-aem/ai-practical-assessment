(function (window) {
    "use strict";

    function escapeHtml(value) {
        return String(value)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#39;");
    }

    function formatStatusLabel(status) {
        return String(status || "")
            .split("_")
            .map(function (part) {
                return part.charAt(0) + part.slice(1).toLowerCase();
            })
            .join(" ");
    }

    function badgeClass(value, type) {
        var normalized = String(value || "").toLowerCase().replace(/_/g, "-");
        return "support-badge support-badge--" + type + " support-badge--" + normalized;
    }

    function renderBadge(value, type) {
        return "<span class=\"" + badgeClass(value, type) + "\">" + escapeHtml(formatStatusLabel(value)) + "</span>";
    }

    function buildDetailUrl(basePath, ticketId) {
        var separator = basePath.indexOf("?") >= 0 ? "&" : "?";
        return basePath + separator + "id=" + encodeURIComponent(ticketId);
    }

    function showMessage(container, message, isError) {
        if (!container) {
            return;
        }
        container.textContent = message;
        container.className = isError ? "support-error" : "support-info";
        container.classList.remove("support-hidden");
    }

    function hideMessage(container) {
        if (container) {
            container.classList.add("support-hidden");
        }
    }

    function showFieldErrors(container, fields) {
        var messages = Object.keys(fields).map(function (key) {
            return key + ": " + fields[key];
        });
        showMessage(container, messages.join(", "), true);
    }

    function setLoading(container, isLoading) {
        if (!container) {
            return;
        }
        var loading = container.querySelector("[data-loading]");
        if (loading) {
            loading.classList.toggle("support-hidden", !isLoading);
        }
        container.classList.toggle("is-loading", isLoading);
    }

    function setButtonsDisabled(root, disabled) {
        if (!root) {
            return;
        }
        root.querySelectorAll("button, input[type='submit']").forEach(function (button) {
            button.disabled = disabled;
        });
    }

    function onReady(callback) {
        if (document.readyState === "loading") {
            document.addEventListener("DOMContentLoaded", callback);
        } else {
            callback();
        }
    }

    function redirectToLogin() {
        var resource = window.location.pathname + window.location.search;
        window.location.href = "/content/support-tickets/login.html?resource="
            + encodeURIComponent(resource);
    }

    window.SupportUi = {
        escapeHtml: escapeHtml,
        formatStatusLabel: formatStatusLabel,
        badgeClass: badgeClass,
        renderBadge: renderBadge,
        buildDetailUrl: buildDetailUrl,
        showMessage: showMessage,
        hideMessage: hideMessage,
        showFieldErrors: showFieldErrors,
        setLoading: setLoading,
        setButtonsDisabled: setButtonsDisabled,
        onReady: onReady,
        redirectToLogin: redirectToLogin
    };
})(window);
