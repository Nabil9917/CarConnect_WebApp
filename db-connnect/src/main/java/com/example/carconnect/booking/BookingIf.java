package com.example.carconnect.booking;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Schnittstelle für Buchungsoperationen, die über RMI (Remote Method Invocation) verfügbar sind.
 * Diese Schnittstelle definiert Methoden zur Verwaltung von Buchungsanfragen, Genehmigungen, Ablehnungen
 * sowie zur Aufzeichnung der Fahrzeugnutzung und zum Abrufen von Nutzungsverlauf.
 *
 */
public interface BookingIf extends Remote {
    boolean insertBookingRequest(String username, int vehicleId, String startTime, String endTime) throws RemoteException;
    boolean approveBookingRequest(int requestId) throws RemoteException;
    boolean rejectBookingRequest(int requestId) throws RemoteException;
    List<Map<String, Object>> getBookingRequests(String username) throws RemoteException;
    int getVehicleIdFromBookingRequest(int requestId) throws RemoteException;
    boolean bookVehicle(String username, int vehicleId, String startTime, String endTime) throws RemoteException;
    boolean recordUsage(String username, int vehicleId, String startTime, String endTime) throws RemoteException;
    List<Map<String, Object>> getUsageHistory(String username) throws RemoteException;
}
