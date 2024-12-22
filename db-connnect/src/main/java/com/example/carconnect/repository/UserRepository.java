package com.example.carconnect.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Repository-Klasse, die für den Zugriff auf die Benutzerdatenbank und die Verwaltung von Benutzerinformationen verantwortlich ist.
 * Diese Klasse enthält Methoden zur Überprüfung der Benutzerexistenz, zum Abrufen von Passwort-Hashes, zur Registrierung neuer Benutzer und zur Aktualisierung von Benutzerpasswörtern.
 */
public class UserRepository {

    private final DBConnectionManager dbConnectionManager;

    public UserRepository(DBConnectionManager dbConnectionManager) {
        this.dbConnectionManager = dbConnectionManager;
    }


    public boolean checkUserExistence(String username) {
        try {
            Connection connection = dbConnectionManager.getConnection();
            String query = "SELECT username FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet res = statement.executeQuery();
            return res.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public String getHash(String username) {
        try {
            Connection connection = dbConnectionManager.getConnection();
            String query = "SELECT hash FROM users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                return res.getString("hash");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean registerNewUser(String username, String hash) {
        try {
            Connection connection = dbConnectionManager.getConnection();
            String query = "INSERT INTO users (username, hash) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, hash);
            statement.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean updateUserPassword(String username, String newPasswordHash) {
        try {
            Connection connection = dbConnectionManager.getConnection();
            String query = "UPDATE users SET hash = ? WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, newPasswordHash);
            statement.setString(2, username);
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
