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

    function renderStatusBadge(element, status) {
        element.className = SupportUi.badgeClass(status, "status");
        element.textContent = SupportUi.formatStatusLabel(status);
    }

    SupportUi.onReady(function () {
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
        var titleHeading = root.querySelector("[data-ticket-title]");

        if (!ticketId) {
            SupportUi.showMessage(message, "Ticket id is required.", true);
            return;
        }

        function renderTicket(ticket) {
            root.querySelector("[data-title]").value = ticket.title;
            root.querySelector("[data-description]").value = ticket.description;
            root.querySelector("[data-priority]").value = ticket.priority;
            renderStatusBadge(root.querySelector("[data-status]"), ticket.status);
            root.querySelector("[data-assignee]").value = ticket.assignedTo;
            root.querySelector("[data-created-by]").textContent = ticket.createdBy;
            root.querySelector("[data-updated-at]").textContent = ticket.updatedAt;
            if (titleHeading) {
                titleHeading.textContent = ticket.title;
            }

            statusActions.innerHTML = "";
            (TRANSITIONS[ticket.status] || []).forEach(function (status) {
                var button = document.createElement("button");
                button.type = "button";
                button.className = "support-btn support-btn--secondary";
                button.textContent = "Move to " + SupportUi.formatStatusLabel(status);
                button.addEventListener("click", function () {
                    SupportUi.setButtonsDisabled(root, true);
                    SupportApi.transitionTicket(ticketId, status)
                        .then(function (updatedTicket) {
                            SupportUi.showMessage(message, "Status updated to " + SupportUi.formatStatusLabel(updatedTicket.status) + ".", false);
                            renderTicket(updatedTicket);
                            loadComments();
                        })
                        .catch(function (error) {
                            SupportUi.showMessage(message, error.payload && error.payload.message ? error.payload.message : error.message, true);
                        })
                        .finally(function () {
                            SupportUi.setButtonsDisabled(root, false);
                        });
                });
                statusActions.appendChild(button);
            });
        }

        function loadComments() {
            SupportApi.listComments(ticketId)
                .then(function (payload) {
                    commentsContainer.innerHTML = "";
                    var items = payload.items || [];
                    if (!items.length) {
                        var empty = document.createElement("p");
                        empty.className = "support-empty";
                        empty.textContent = "No comments yet.";
                        commentsContainer.appendChild(empty);
                        return;
                    }
                    items.forEach(function (comment) {
                        var item = document.createElement("article");
                        item.className = "support-comment";
                        var authorInitial = (comment.createdBy || "?").charAt(0).toUpperCase();
                        item.innerHTML =
                            "<div class=\"support-comment__header\">" +
                                "<span class=\"support-comment__avatar\">" + SupportUi.escapeHtml(authorInitial) + "</span>" +
                                "<div class=\"support-comment__meta\">" +
                                    "<span class=\"support-comment__author\">" + SupportUi.escapeHtml(comment.createdBy) + "</span>" +
                                    " · " + SupportUi.escapeHtml(comment.createdAt || "") +
                                "</div>" +
                            "</div>" +
                            "<p class=\"support-comment__message\">" + SupportUi.escapeHtml(comment.message) + "</p>";
                        commentsContainer.appendChild(item);
                    });
                })
                .catch(function (error) {
                    SupportUi.showMessage(message, error.payload && error.payload.message ? error.payload.message : error.message, true);
                });
        }

        SupportUi.setButtonsDisabled(root, true);
        SupportUi.showMessage(message, "Loading ticket...", false);

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
                SupportUi.hideMessage(message);
                renderTicket(ticket);
                loadComments();
            })
            .catch(function (error) {
                SupportUi.showMessage(message, error.payload && error.payload.message ? error.payload.message : error.message, true);
            })
            .finally(function () {
                SupportUi.setButtonsDisabled(root, false);
            });

        updateForm.addEventListener("submit", function (event) {
            event.preventDefault();
            var payload = {
                title: root.querySelector("[data-title]").value,
                description: root.querySelector("[data-description]").value,
                priority: root.querySelector("[data-priority]").value,
                assignedTo: root.querySelector("[data-assignee]").value
            };
            SupportUi.setButtonsDisabled(root, true);
            SupportApi.updateTicket(ticketId, payload)
                .then(function (ticket) {
                    SupportUi.showMessage(message, "Ticket updated.", false);
                    renderTicket(ticket);
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
                });
        });

        commentForm.addEventListener("submit", function (event) {
            event.preventDefault();
            var messageInput = root.querySelector("[data-comment-message]");
            SupportUi.setButtonsDisabled(root, true);
            SupportApi.addComment(ticketId, messageInput.value)
                .then(function () {
                    messageInput.value = "";
                    loadComments();
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
                });
        });
    });
})(document);
