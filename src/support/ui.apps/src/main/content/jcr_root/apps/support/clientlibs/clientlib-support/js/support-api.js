(function (window, document) {
    "use strict";

    var API_BASE = "/bin/api/support";
    var csrfTokenPromise;

    function getCsrfToken() {
        var meta = document.querySelector('meta[name="csrf-token"]');
        if (meta && meta.getAttribute("content")) {
            return Promise.resolve(meta.getAttribute("content"));
        }
        if (window.CSRF_TOKEN) {
            return Promise.resolve(window.CSRF_TOKEN);
        }
        if (!csrfTokenPromise) {
            csrfTokenPromise = fetch("/libs/granite/csrf/token.json", {
                credentials: "same-origin"
            }).then(function (response) {
                return response.json();
            }).then(function (data) {
                return data.token || "";
            });
        }
        return csrfTokenPromise;
    }

    function ticketPath(id, action) {
        var path = "/tickets." + encodeURIComponent(id);
        if (action) {
            path += "." + action;
        }
        return path + ".json";
    }

    function request(method, path, body) {
        var isMutating = method !== "GET";
        var tokenPromise = isMutating ? getCsrfToken() : Promise.resolve("");

        return tokenPromise.then(function (token) {
            var headers = {
                "Content-Type": "application/json"
            };
            if (token) {
                headers["CSRF-Token"] = token;
            }
            return fetch(API_BASE + path, {
                method: method,
                headers: headers,
                credentials: "same-origin",
                body: body ? JSON.stringify(body) : undefined
            });
        }).then(function (response) {
            return response.json().then(function (payload) {
                if (!response.ok) {
                    if (response.status === 401) {
                        SupportUi.redirectToLogin();
                    }
                    var error = new Error(payload.message || "Request failed");
                    error.status = response.status;
                    error.payload = payload;
                    throw error;
                }
                return payload;
            });
        });
    }

    window.SupportApi = {
        listTickets: function (query, status) {
            var params = [];
            if (query) {
                params.push("q=" + encodeURIComponent(query));
            }
            if (status) {
                params.push("status=" + encodeURIComponent(status));
            }
            var suffix = params.length ? "?" + params.join("&") : "";
            return request("GET", "/tickets.json" + suffix);
        },
        getTicket: function (id) {
            return request("GET", ticketPath(id));
        },
        createTicket: function (payload) {
            return request("POST", "/tickets.json", payload);
        },
        updateTicket: function (id, payload) {
            return request("PATCH", ticketPath(id), payload);
        },
        transitionTicket: function (id, status) {
            return request("POST", ticketPath(id, "status"), { status: status });
        },
        listComments: function (id) {
            return request("GET", ticketPath(id, "comments"));
        },
        addComment: function (id, message) {
            return request("POST", ticketPath(id, "comments"), { message: message });
        },
        listUsers: function () {
            return request("GET", "/users");
        }
    };
})(window, document);
