(function (document) {
    "use strict";

    function renderRows(tableBody, items, detailPagePath) {
        tableBody.innerHTML = "";
        if (!items.length) {
            var row = document.createElement("tr");
            var cell = document.createElement("td");
            cell.colSpan = 5;
            cell.className = "support-empty";
            cell.textContent = "No tickets found.";
            row.appendChild(cell);
            tableBody.appendChild(row);
            return;
        }
        items.forEach(function (ticket) {
            var row = document.createElement("tr");
            var detailUrl = SupportUi.buildDetailUrl(detailPagePath, ticket.id);
            row.innerHTML =
                "<td><a href=\"" + SupportUi.escapeHtml(detailUrl) + "\">" + SupportUi.escapeHtml(ticket.title) + "</a></td>" +
                "<td>" + SupportUi.renderBadge(ticket.status, "status") + "</td>" +
                "<td>" + SupportUi.renderBadge(ticket.priority, "priority") + "</td>" +
                "<td>" + SupportUi.escapeHtml(ticket.assignedTo) + "</td>" +
                "<td>" + SupportUi.escapeHtml(ticket.updatedAt || "") + "</td>";
            tableBody.appendChild(row);
        });
    }

    SupportUi.onReady(function () {
        var root = document.querySelector("[data-support-ticket-list]");
        if (!root) {
            return;
        }
        var searchInput = root.querySelector("[data-search]");
        var statusSelect = root.querySelector("[data-status-filter]");
        var searchButton = root.querySelector("[data-search-button]");
        var tableBody = root.querySelector("[data-results]");
        var message = root.querySelector("[data-message]");
        var loadingContainer = root.querySelector("[data-loading-container]");
        var detailPagePath = root.getAttribute("data-detail-page") || "/content/support-tickets/detail.html";
        var debounceTimer;

        function loadTickets() {
            SupportUi.setLoading(loadingContainer, true);
            SupportUi.showMessage(message, "Loading tickets...", false);
            SupportApi.listTickets(searchInput.value, statusSelect.value)
                .then(function (payload) {
                    SupportUi.hideMessage(message);
                    renderRows(tableBody, payload.items || [], detailPagePath);
                })
                .catch(function (error) {
                    SupportUi.showMessage(message, error.payload && error.payload.message ? error.payload.message : error.message, true);
                    tableBody.innerHTML = "";
                })
                .finally(function () {
                    SupportUi.setLoading(loadingContainer, false);
                });
        }

        searchButton.addEventListener("click", loadTickets);
        searchInput.addEventListener("keydown", function (event) {
            if (event.key === "Enter") {
                event.preventDefault();
                loadTickets();
            }
        });
        searchInput.addEventListener("input", function () {
            window.clearTimeout(debounceTimer);
            debounceTimer = window.setTimeout(loadTickets, 300);
        });
        statusSelect.addEventListener("change", loadTickets);
        loadTickets();
    });
})(document);
