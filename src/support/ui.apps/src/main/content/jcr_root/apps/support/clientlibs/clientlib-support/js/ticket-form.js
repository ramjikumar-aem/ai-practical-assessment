(function (document) {
    "use strict";

    function showMessage(container, message, isError) {
        container.textContent = message;
        container.className = isError ? "support-error" : "support-info";
        container.classList.remove("support-hidden");
    }

    function showFieldErrors(container, fields) {
        var messages = Object.keys(fields).map(function (key) {
            return key + ": " + fields[key];
        });
        showMessage(container, messages.join(", "), true);
    }

    document.addEventListener("DOMContentLoaded", function () {
        var root = document.querySelector("[data-support-ticket-form]");
        if (!root) {
            return;
        }
        var assigneeSelect = root.querySelector("[data-assignee]");
        var message = root.querySelector("[data-message]");
        var form = root.querySelector("form");

        SupportApi.listUsers()
            .then(function (payload) {
                (payload.items || []).forEach(function (user) {
                    var option = document.createElement("option");
                    option.value = user.id;
                    option.textContent = user.name + " (" + user.role + ")";
                    assigneeSelect.appendChild(option);
                });
            })
            .catch(function () {
                showMessage(message, "Unable to load assignable users.", true);
            });

        form.addEventListener("submit", function (event) {
            event.preventDefault();
            var payload = {
                title: root.querySelector("[data-title]").value,
                description: root.querySelector("[data-description]").value,
                priority: root.querySelector("[data-priority]").value,
                assignedTo: assigneeSelect.value
            };
            SupportApi.createTicket(payload)
                .then(function (ticket) {
                    window.location.href = "/content/support-tickets/detail.html?id=" + encodeURIComponent(ticket.id);
                })
                .catch(function (error) {
                    if (error.payload && error.payload.fields) {
                        showFieldErrors(message, error.payload.fields);
                    } else {
                        showMessage(message, error.payload && error.payload.message ? error.payload.message : error.message, true);
                    }
                });
        });
    });
})(document);
