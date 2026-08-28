(function (document) {
    "use strict";

    function showMessage(container, message, isError) {
        container.textContent = message;
        container.className = isError ? "support-error" : "support-info";
        container.classList.remove("support-hidden");
    }

    function renderRows(tableBody, items) {
        tableBody.innerHTML = "";
        if (!items.length) {
            var row = document.createElement("tr");
            var cell = document.createElement("td");
            cell.colSpan = 5;
            cell.textContent = "No tickets found.";
            row.appendChild(cell);
            tableBody.appendChild(row);
            return;
        }
        items.forEach(function (ticket) {
            var row = document.createElement("tr");
            row.innerHTML =
                "<td><a href=\"/content/support-tickets/detail.html?id=" + encodeURIComponent(ticket.id) + "\">" +
                ticket.title + "</a></td>" +
                "<td>" + ticket.status + "</td>" +
                "<td>" + ticket.priority + "</td>" +
                "<td>" + ticket.assignedTo + "</td>" +
                "<td>" + ticket.updatedAt + "</td>";
            tableBody.appendChild(row);
        });
    }

    document.addEventListener("DOMContentLoaded", function () {
        var root = document.querySelector("[data-support-ticket-list]");
        if (!root) {
            return;
        }
        var searchInput = root.querySelector("[data-search]");
        var statusSelect = root.querySelector("[data-status-filter]");
        var searchButton = root.querySelector("[data-search-button]");
        var tableBody = root.querySelector("[data-results]");
        var message = root.querySelector("[data-message]");

        function loadTickets() {
            showMessage(message, "Loading tickets...", false);
            SupportApi.listTickets(searchInput.value, statusSelect.value)
                .then(function (payload) {
                    message.classList.add("support-hidden");
                    renderRows(tableBody, payload.items || []);
                })
                .catch(function (error) {
                    showMessage(message, error.payload && error.payload.message ? error.payload.message : error.message, true);
                });
        }

        searchButton.addEventListener("click", loadTickets);
        loadTickets();
    });
})(document);
