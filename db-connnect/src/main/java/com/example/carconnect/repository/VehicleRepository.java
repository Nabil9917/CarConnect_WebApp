package com.example.carconnect.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository-Klasse, die für den Zugriff auf die Fahrzeugdatenbank und die Verwaltung von Fahrzeuginformationen verantwortlich ist.
 * Diese Klasse enthält Methoden zum Registrieren, Suchen, Aktualisieren, Löschen und Überprüfen der Verfügbarkeit von Fahrzeugen.
 *
 */
public class VehicleRepository {

    private final DBConnectionManager dbConnectionManager;


    public VehicleRepository(DBConnectionManager dbConnectionManager) {
        this.dbConnectionManager = dbConnectionManager;
    }

    public boolean registerVehicle(String ownerUsername, String make, String model, int year, String location) {
        try {
            Connection connection = dbConnectionManager.getConnection();
            String userQuery = "SELECT id FROM users WHERE username = ?";
            PreparedStatement userStatement = connection.prepareStatement(userQuery);
            userStatement.setString(1, ownerUsername);
            ResultSet userResult = userStatement.executeQuery();

            if (userResult.next()) {
                int ownerId = userResult.getInt("id");

                String query = "INSERT INTO vehicles (owner_id, make, model, year, location, available) VALUES (?, ?, ?, ?, ?, true)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, ownerId);
                statement.setString(2, make);
                statement.setString(3, model);
                statement.setInt(4, year);
                statement.setString(5, location);
                statement.execute();
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Map<String, Object>> searchAvailableVehicles(Map<String, String> searchCriteria) {
        List<Map<String, Object>> vehicles = new ArrayList<>();
        try {
            Connection connection = dbConnectionManager.getConnection();

            StringBuilder query = new StringBuilder("SELECT v.*, u.username AS owner_name FROM vehicles v JOIN users u ON v.owner_id = u.id WHERE v.available = true");

            for (Map.Entry<String, String> entry : searchCriteria.entrySet()) {
                if (entry.getKey().equals("year")) {
                    query.append(" AND ").append(entry.getKey()).append(" = ?");
                } else {
                    query.append(" AND LOWER(").append(entry.getKey()).append(") LIKE ?");
                }
            }

            PreparedStatement statement = connection.prepareStatement(query.toString());

            int index = 1;
            for (Map.Entry<String, String> entry : searchCriteria.entrySet()) {
                if (entry.getKey().equals("year")) {
                    statement.setInt(index++, Integer.parseInt(entry.getValue()));
                } else {
                    statement.setString(index++, "%" + entry.getValue() + "%");
                }
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Map<String, Object> vehicle = new HashMap<>();
                vehicle.put("id", resultSet.getInt("id"));
                vehicle.put("ownerName", resultSet.getString("owner_name"));
                vehicle.put("make", resultSet.getString("make"));
                vehicle.put("model", resultSet.getString("model"));
                vehicle.put("year", resultSet.getInt("year"));
                vehicle.put("location", resultSet.getString("location"));
                vehicle.put("available", resultSet.getBoolean("available"));
                vehicles.add(vehicle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }


    public boolean updateVehicle(int vehicleId, String make, String model, int year, String location) {
        try {
            Connection connection = dbConnectionManager.getConnection();
            String query = "UPDATE vehicles SET make = ?, model = ?, year = ?, location = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, make);
            statement.setString(2, model);
            statement.setInt(3, year);
            statement.setString(4, location);
            statement.setInt(5, vehicleId);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteVehicle(int vehicleId) {
        try {
            Connection connection = dbConnectionManager.getConnection();

            String deleteBookingRequestsQuery = "DELETE FROM booking_requests WHERE vehicle_id = ?";
            PreparedStatement deleteBookingRequestsStatement = connection.prepareStatement(deleteBookingRequestsQuery);
            deleteBookingRequestsStatement.setInt(1, vehicleId);
            deleteBookingRequestsStatement.executeUpdate();

            String query = "DELETE FROM vehicles WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, vehicleId);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean updateVehicleAvailability(int vehicleId, boolean available) {
        try {
            Connection connection = dbConnectionManager.getConnection();
            String query = "UPDATE vehicles SET available = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setBoolean(1, available);
            statement.setInt(2, vehicleId);
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean isVehicleOwner(String username, int vehicleId) {
        try {
            Connection connection = dbConnectionManager.getConnection();
            String query = "SELECT owner_id FROM vehicles WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, vehicleId);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                int ownerId = res.getInt("owner_id");
                String ownerQuery = "SELECT username FROM users WHERE id = ?";
                PreparedStatement ownerStatement = connection.prepareStatement(ownerQuery);
                ownerStatement.setInt(1, ownerId);
                ResultSet ownerRes = ownerStatement.executeQuery();
                if (ownerRes.next()) {
                    return ownerRes.getString("username").equals(username);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getLastInsertedVehicleId() {
        try {
            Connection connection = dbConnectionManager.getConnection();
            String query = "SELECT currval(pg_get_serial_sequence('vehicles', 'id'))";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getVehicleIdFromBookingRequest(int requestId) {
        try {
            Connection connection = dbConnectionManager.getConnection();
            String query = "SELECT vehicle_id FROM booking_requests WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, requestId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("vehicle_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
