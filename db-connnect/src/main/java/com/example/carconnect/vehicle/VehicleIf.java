package com.example.carconnect.vehicle;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Schnittstelle für die Fahrzeugverwaltung, die über RMI (Remote Method Invocation) verfügbar ist.
 * Diese Schnittstelle definiert Methoden zur Registrierung, Suche, Aktualisierung, Löschung von Fahrzeugen
 * sowie zur Überprüfung der Fahrzeugbesitzer und zum Abrufen der letzten eingefügten Fahrzeug-ID.
 */
public interface VehicleIf extends Remote {

    boolean registerVehicle(String ownerUsername, String make, String model, int year, String location) throws RemoteException;
    List<Map<String, Object>> searchAvailableVehicles(Map<String, String> searchCriteria) throws RemoteException;
    boolean updateVehicle(int vehicleId, String make, String model, int year, String location) throws RemoteException;
    boolean deleteVehicle(int vehicleId) throws RemoteException;
    boolean isVehicleOwner(String username, int vehicleId) throws RemoteException;
    int getLastInsertedVehicleId() throws RemoteException;
}
