package com.example.carconnect.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnectionManager ist eine Klasse, die eine Verbindung zur Datenbank verwaltet.
 * Sie stellt sicher, dass nur eine einzige Instanz der Datenbankverbindung verwendet wird (Singleton-Pattern).
 * Diese Klasse verwendet PostgreSQL als Datenbankmanagementsystem.
 */
public class DBConnectionManager {


    private static final String DB_URL = "jdbc:postgresql://localhost:5432/carconnectdb";
    private static final String USER = "carconnectuser";
    private static final String PASSWORD = "12345";
    private static Connection connection;


    public DBConnectionManager() {
        getConnection();
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}
