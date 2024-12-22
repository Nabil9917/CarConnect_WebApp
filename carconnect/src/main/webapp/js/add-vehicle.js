// add-vehicle.js
document.addEventListener("DOMContentLoaded", function() {
    const registerVehicleForm = document.getElementById("registerVehicleForm");


    let authToken = localStorage.getItem('token');
    let username = localStorage.getItem('username');


    if (registerVehicleForm) {
        registerVehicleForm.addEventListener("submit", function(event) {
            event.preventDefault();
            const make = document.getElementById("make").value;
            const model = document.getElementById("model").value;
            const year = document.getElementById("year").value;
            const location = document.getElementById("location").value;


            fetch("http://localhost:8080/carconnect_war_exploded/registerVehicle", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                    "Authorization": `Bearer ${authToken}`
                },
                body: `ownerUsername=${username}&make=${make}&model=${model}&year=${year}&location=${location}`
            })
                .then(response => response.json())
                .then(data => {
                    alert("Vehicle registration " + (data.success ? "successful" : "failed"));
                });
        });
    }
});
