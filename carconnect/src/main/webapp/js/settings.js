// settings.js
document.addEventListener("DOMContentLoaded", function() {
    const authToken = localStorage.getItem('token');
    const username = localStorage.getItem('username');

    if (!authToken || !username) {
        window.location.href = 'login.html';
    }

    document.getElementById('usernameDisplay').innerText = username;
    document.getElementById('changePasswordForm').addEventListener('submit', function(event) {
        event.preventDefault();
        const oldPassword = document.getElementById('oldPassword').value;
        const newPassword = document.getElementById('newPassword').value;
        const confirmNewPassword = document.getElementById('confirmNewPassword').value;

        if (newPassword !== confirmNewPassword) {
            alert('New password and confirmation do not match.');
            return;
        }

        fetch("http://localhost:8080/carconnect_war_exploded/changePassword", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                "Authorization": `Bearer ${authToken}`
            },
            body: `username=${username}&oldPassword=${encodeURIComponent(oldPassword)}&newPassword=${encodeURIComponent(newPassword)}`
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Password changed successfully.');
                    document.getElementById('changePasswordForm').reset(); // Zurücksetzen des Formulars
                } else {
                    alert('Password change failed: ' + (data.message || 'Unknown error'));
                }
            })
            .catch(error => console.error('Error changing password:', error)); // Fehlerbehandlung
    });

    document.getElementById('logoutButton').addEventListener('click', function() {
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        window.location.href = 'login.html';
    });
});
