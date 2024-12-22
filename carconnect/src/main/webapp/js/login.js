// login.js
document.addEventListener("DOMContentLoaded", function() {
    // Einrichtung der WebSocket-Verbindung für Benachrichtigungen
    const socket = new WebSocket("ws://localhost:8080/carconnect_war_exploded/notifications");

    socket.onopen = function() {
        console.log("WebSocket connection established");
    };

    socket.onmessage = function(event) {
        console.log("WebSocket message received:", event.data);
        fetchAllVehicles();
        fetchUsageHistory();
        fetchBookingRequests();
    };

    socket.onclose = function() {
        console.log("WebSocket connection closed");
    };

    socket.onerror = function(error) {
        console.error("WebSocket error:", error);
    };

    const registerForm = document.getElementById("registerForm");
    const loginForm = document.getElementById("loginForm");
    let authToken = localStorage.getItem('token');

    if (registerForm) {
        registerForm.addEventListener("submit", function(event) {
            event.preventDefault();
            const username = document.getElementById("registerUsername").value;
            const password = document.getElementById("registerPassword").value;

            // Senden der Registrierungsdaten an den Server
            fetch("http://localhost:8080/carconnect_war_exploded/auth", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: `action=register&username=${username}&password=${password}`
            })
                .then(response => response.json())
                .then(data => {
                    alert("Registration " + (data.success ? "successful" : "failed"));
                });
        });
    }

    if (loginForm) {
        loginForm.addEventListener("submit", function(event) {
            event.preventDefault();
            const username = document.getElementById("loginUsername").value;
            const password = document.getElementById("loginPassword").value;

            fetch("http://localhost:8080/carconnect_war_exploded/auth", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: `action=login&username=${username}&password=${password}`
            })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        authToken = data.token;
                        localStorage.setItem('token', authToken);
                        localStorage.setItem('username', username);
                        alert("Login successful");
                        window.location.href = "landing.html";
                    } else {
                        alert("Login failed");
                    }
                });
        });
    }
});
