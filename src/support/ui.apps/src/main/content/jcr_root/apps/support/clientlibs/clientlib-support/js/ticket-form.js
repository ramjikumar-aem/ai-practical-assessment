(function (document) {
    "use strict";

    SupportUi.onReady(function () {
        var root = document.querySelector("[data-support-ticket-form]");
        if (!root) {
            return;
        }
        var assigneeSelect = root.querySelector("[data-assignee]");
        var message = root.querySelector("[data-message]");
        var form = root.querySelector("form");
        var submitButton = root.querySelector("[data-submit]");
        var detailPagePath = root.getAttribute("data-detail-page") || "/content/support-tickets/detail.html";

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
                SupportUi.showMessage(message, "Unable to load assignable users.", true);
            });

        form.addEventListener("submit", function (event) {
            event.preventDefault();
            var payload = {
                title: root.querySelector("[data-title]").value,
                description: root.querySelector("[data-description]").value,
                priority: root.querySelector("[data-priority]").value,
                assignedTo: assigneeSelect.value
            };
            SupportUi.setButtonsDisabled(root, true);
            if (submitButton) {
                submitButton.textContent = "Creating...";
            }
            SupportApi.createTicket(payload)
                .then(function (ticket) {
                    window.location.href = SupportUi.buildDetailUrl(detailPagePath, ticket.id);
                })
                .catch(function (error) {
                    if (error.payload && error.payload.fields) {
                        SupportUi.showFieldErrors(message, error.payload.fields);
                    } else {
                        SupportUi.showMessage(message, error.payload && error.payload.message ? error.payload.message : error.message, true);
                    }
                })
                .finally(function () {
                    SupportUi.setButtonsDisabled(root, false);
                    if (submitButton) {
                        submitButton.textContent = "Create Ticket";
                    }
                });
        });
    });
})(document);
