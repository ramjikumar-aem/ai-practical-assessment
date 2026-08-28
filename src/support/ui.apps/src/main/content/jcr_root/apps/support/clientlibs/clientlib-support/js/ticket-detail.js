(function (document) {
    "use strict";

    var TRANSITIONS = {
        OPEN: ["IN_PROGRESS", "CANCELLED"],
        IN_PROGRESS: ["RESOLVED", "CANCELLED"],
        RESOLVED: ["CLOSED"],
        CLOSED: [],
        CANCELLED: []
    };

    function getTicketId() {
        return new URLSearchParams(window.location.search).get("id");
    }

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
        var root = document.querySelector("[data-support-ticket-detail]");
        if (!root) {
            return;
        }
        var ticketId = getTicketId();
        var message = root.querySelector("[data-message]");
        var statusActions = root.querySelector("[data-status-actions]");
        var commentsContainer = root.querySelector("[data-comments]");
        var commentForm = root.querySelector("[data-comment-form]");
        var updateForm = root.querySelector("[data-update-form]");

        if (!ticketId) {
            showMessage(message, "Ticket id is required.", true);
            return;
        }

        function renderTicket(ticket) {
            root.querySelector("[data-title]").value = ticket.title;
            root.querySelector("[data-description]").value = ticket.description;
            root.querySelector("[data-priority]").value = ticket.priority;
            root.querySelector("[data-status]").textContent = ticket.status;
            root.querySelector("[data-assignee]").value = ticket.assignedTo;
            root.querySelector("[data-created-by]").textContent = ticket.createdBy;
            root.querySelector("[data-updated-at]").textContent = ticket.updatedAt;

            statusActions.innerHTML = "";
            (TRANSITIONS[ticket.status] || []).forEach(function (status) {
                var button = document.createElement("button");
                button.type = "button";
                button.textContent = "Move to " + status;
                button.addEventListener("click", function () {
                    SupportApi.transitionTicket(ticketId, status)
                        .then(renderTicket)
                        .catch(function (error) {
                            showMessage(message, error.payload && error.payload.message ? error.payload.message : error.message, true);
                        });
                });
                statusActions.appendChild(button);
            });
        }

        function loadComments() {
            SupportApi.listComments(ticketId)
                .then(function (payload) {
                    commentsContainer.innerHTML = "";
                    (payload.items || []).forEach(function (comment) {
                        var item = document.createElement("div");
                        item.className = "support-comment";
                        item.innerHTML = "<strong>" + comment.createdBy + "</strong> (" + comment.createdAt + ")<p>" + comment.message + "</p>";
                        commentsContainer.appendChild(item);
                    });
                })
                .catch(function (error) {
                    showMessage(message, error.payload && error.payload.message ? error.payload.message : error.message, true);
                });
        }

        SupportApi.listUsers()
            .then(function (payload) {
                var assigneeSelect = root.querySelector("[data-assignee]");
                (payload.items || []).forEach(function (user) {
                    var option = document.createElement("option");
                    option.value = user.id;
                    option.textContent = user.name + " (" + user.role + ")";
                    assigneeSelect.appendChild(option);
                });
                return SupportApi.getTicket(ticketId);
            })
            .then(function (ticket) {
                message.classList.add("support-hidden");
                renderTicket(ticket);
                loadComments();
            })
            .catch(function (error) {
                showMessage(message, error.payload && error.payload.message ? error.payload.message : error.message, true);
            });

        updateForm.addEventListener("submit", function (event) {
            event.preventDefault();
            var payload = {
                title: root.querySelector("[data-title]").value,
                description: root.querySelector("[data-description]").value,
                priority: root.querySelector("[data-priority]").value,
                assignedTo: root.querySelector("[data-assignee]").value
            };
            SupportApi.updateTicket(ticketId, payload)
                .then(function (ticket) {
                    showMessage(message, "Ticket updated.", false);
                    renderTicket(ticket);
                })
                .catch(function (error) {
                    if (error.payload && error.payload.fields) {
                        showFieldErrors(message, error.payload.fields);
                    } else {
                        showMessage(message, error.payload && error.payload.message ? error.payload.message : error.message, true);
                    }
                });
        });

        commentForm.addEventListener("submit", function (event) {
            event.preventDefault();
            var messageInput = root.querySelector("[data-comment-message]");
            SupportApi.addComment(ticketId, messageInput.value)
                .then(function () {
                    messageInput.value = "";
                    loadComments();
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
