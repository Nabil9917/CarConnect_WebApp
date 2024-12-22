// profile.js
document.addEventListener("DOMContentLoaded", function() {
    const socket = new WebSocket("ws://localhost:8080/carconnect_war_exploded/notifications");

    socket.onopen = function() {
        console.log("WebSocket connection established");
    };

    socket.onmessage = function(event) {
        console.log("WebSocket message received:", event.data);
        fetchBookingRequests();
        fetchUsageHistory();
    };
    socket.onclose = function() {
        console.log("WebSocket connection closed");
    };

    socket.onerror = function(error) {
        console.error("WebSocket error:", error);
    };

    function fetchUsageHistory() {
        fetch(`http://localhost:8080/carconnect_war_exploded/usageHistory?username=${localStorage.getItem('username')}`, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${localStorage.getItem('token')}`
            }
        })
            .then(response => response.json())
            .then(data => {
                const tbody = document.getElementById("usageHistoryTableBody");
                tbody.innerHTML = "";
                data.forEach(item => {
                    const row = document.createElement("tr");
                    row.innerHTML = `<td>${item.make} ${item.model} (${item.year})</td><td>${item.startTime}</td><td>${item.endTime}</td>`;
                    tbody.appendChild(row);
                });
            })
            .catch(error => console.error('Error fetching usage history:', error));
    }

    function fetchBookingRequests() {
        fetch(`http://localhost:8080/carconnect_war_exploded/bookingRequests?username=${localStorage.getItem('username')}`, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${localStorage.getItem('token')}`
            }
        })
            .then(response => response.json())
            .then(data => {
                const tbody = document.getElementById("bookingRequestsTableBody");
                tbody.innerHTML = "";
                data.forEach(request => {
                    const row = document.createElement("tr");
                    row.innerHTML = `
                    <td>${request.id}</td>
                    <td>${request.vehicle}</td>
                    <td>${request.requester}</td>
                    <td>${request.start_time}</td>
                    <td>${request.end_time}</td>
                    <td>${request.status}</td>
                    <td id="action-buttons-${request.id}">
                        ${request.status === 'PENDING' ? `
                        <button class="btn-approve" onclick="approveBooking(${request.id})">Approve</button>
                        <button class="btn-reject" onclick="rejectBooking(${request.id})">Reject</button>
                        ` : ''}
                    </td>
                    `;
                    tbody.appendChild(row);
                });
            })
            .catch(error => console.error('Error fetching booking requests:', error));
    }

    window.approveBooking = function(requestId) {
        fetch("http://localhost:8080/carconnect_war_exploded/bookVehicle", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "Authorization": `Bearer ${localStorage.getItem('token')}`
            },
            body: `action=approve&requestId=${requestId}`
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert("Booking approved");
                    document.getElementById(`action-buttons-${requestId}`).innerHTML = '';
                    socket.send("refresh"); // Aufforderung zur Aktualisierung über WebSocket
                } else {
                    alert("Failed to approve booking: " + (data.message || "Unknown error"));
                }
            })
            .catch(error => console.error('Error approving booking:', error));
    };

    window.rejectBooking = function(requestId) {
        fetch("http://localhost:8080/carconnect_war_exploded/bookVehicle", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "Authorization": `Bearer ${localStorage.getItem('token')}`
            },
            body: `action=reject&requestId=${requestId}`
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert("Booking rejected");
                    document.getElementById(`action-buttons-${requestId}`).innerHTML = '';
                    socket.send("refresh"); // Aufforderung zur Aktualisierung über WebSocket
                } else {
                    alert("Failed to reject booking: " + (data.message || "Unknown error"));
                }
            })
            .catch(error => console.error('Error rejecting booking:', error));
    };

    fetchUsageHistory();
    fetchBookingRequests();
});
