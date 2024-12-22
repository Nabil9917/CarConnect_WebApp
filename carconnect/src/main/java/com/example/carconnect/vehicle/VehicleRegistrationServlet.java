package com.example.carconnect.vehicle;

import com.example.carconnect.websocket.NotificationEndpoint;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;

/**
 * Servlet zur Verwaltung der Fahrzeugregistrierung.
 * Dieses Servlet ermöglicht es, neue Fahrzeuge zu registrieren und sendet nach erfolgreicher Registrierung eine Benachrichtigung über Websockets.
 */
@WebServlet(name = "VehicleRegistrationServlet", urlPatterns = {"/registerVehicle"})
public class VehicleRegistrationServlet extends HttpServlet {
    private VehicleIf vehicleIf;

    @Override
    public void init() throws ServletException {
        try {
            vehicleIf = (VehicleIf) Naming.lookup("rmi://localhost:1099/VehicleIf");
            System.out.println("RMI lookup successful"); // Debugging Log
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Failed to lookup RMI server", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String ownerUsername = request.getParameter("ownerUsername");
        String make = request.getParameter("make");
        String model = request.getParameter("model");
        int year = Integer.parseInt(request.getParameter("year"));
        String location = request.getParameter("location");

        boolean result = false;
        try {
            System.out.println("Registering vehicle: " + make + " " + model + " " + year + " " + location);
            result = vehicleIf.registerVehicle(ownerUsername, make, model, year, location);
            if (result) {
                int vehicleId = vehicleIf.getLastInsertedVehicleId();
                JSONObject newVehicle = new JSONObject();
                newVehicle.put("vehicleId", vehicleId);
                newVehicle.put("ownerName", ownerUsername);
                newVehicle.put("make", make);
                newVehicle.put("model", model);
                newVehicle.put("year", year);
                newVehicle.put("location", location);
                newVehicle.put("action", "add");
                NotificationEndpoint.sendNotification(newVehicle.toString());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("success", result);
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse.toString());
    }
}
