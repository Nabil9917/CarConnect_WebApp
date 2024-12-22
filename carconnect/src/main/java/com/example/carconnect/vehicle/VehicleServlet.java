package com.example.carconnect.vehicle;

import com.example.carconnect.booking.BookingIf;
import com.example.carconnect.websocket.NotificationEndpoint;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet zur Verwaltung von Fahrzeugoperationen wie Suche, Aktualisierung, Löschung und Buchung.
 * Das Servlet kommuniziert über RMI mit dem Fahrzeug- und Buchungsservice und sendet Benachrichtigungen
 * über WebSocket-Verbindungen.
 */
@WebServlet(name = "VehicleServlet", urlPatterns = {"/vehicles"})
public class VehicleServlet extends HttpServlet {
    private VehicleIf vehicleIf;
    private BookingIf bookingIf;



    @Override
    public void init() throws ServletException {
        try {
            vehicleIf = (VehicleIf) Naming.lookup("rmi://localhost:1099/VehicleIf");
        } catch (Exception e) {
            throw new ServletException("Failed to lookup RMI server", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONObject jsonResponse = new JSONObject();

        try {
            List<Map<String, Object>> vehicles = vehicleIf.searchAvailableVehicles(new HashMap<>());
            JSONArray vehicleArray = new JSONArray();
            for (Map<String, Object> vehicle : vehicles) {
                JSONObject vehicleJson = new JSONObject(vehicle);
                vehicleArray.put(vehicleJson);
            }
            jsonResponse.put("vehicles", vehicleArray);
            System.out.println("Vehicles found: " + vehicleArray.length());
        } catch (RemoteException e) {
            jsonResponse.put("error", e.getMessage());
        }

        response.setContentType("application/json");
        response.getWriter().write(jsonResponse.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String username = request.getParameter("username");
        boolean result = false;
        JSONObject jsonResponse = new JSONObject();

        try {
            int vehicleId = Integer.parseInt(request.getParameter("vehicleId"));
            System.out.println("Received request for action: " + action + " by user: " + username + " for vehicleId: " + vehicleId);

            if (vehicleIf.isVehicleOwner(username, vehicleId) || action.equals("book")) {
                switch (action) {
                    case "update":
                        String make = request.getParameter("make");
                        String model = request.getParameter("model");
                        int year = Integer.parseInt(request.getParameter("year"));
                        String location = request.getParameter("location");
                        result = vehicleIf.updateVehicle(vehicleId, make, model, year, location);
                        if (result) {
                            JSONObject updatedVehicle = new JSONObject();
                            updatedVehicle.put("vehicleId", vehicleId);
                            updatedVehicle.put("make", make);
                            updatedVehicle.put("model", model);
                            updatedVehicle.put("year", year);
                            updatedVehicle.put("location", location);
                            updatedVehicle.put("action", "update");

                            NotificationEndpoint.sendNotification(updatedVehicle.toString());
                        }
                        break;
                    case "delete":
                        result = vehicleIf.deleteVehicle(vehicleId);
                        if (result) {
                            JSONObject deletedVehicle = new JSONObject();
                            deletedVehicle.put("vehicleId", vehicleId);
                            deletedVehicle.put("action", "delete");

                            NotificationEndpoint.sendNotification(deletedVehicle.toString());
                        }
                        break;
                    case "book":
                        String startTime = request.getParameter("startTime");
                        String endTime = request.getParameter("endTime");
                        result = bookingIf.bookVehicle(username, vehicleId, startTime, endTime);
                        break;
                    default:
                        jsonResponse.put("error", "Invalid action");
                }
            } else {
                jsonResponse.put("error", "Unauthorized action");
                System.out.println("Unauthorized action attempted by user: " + username + " for vehicleId: " + vehicleId);
            }
        } catch (RemoteException e) {
            jsonResponse.put("error", e.getMessage());
        }

        jsonResponse.put("success", result);
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse.toString());
    }
}
