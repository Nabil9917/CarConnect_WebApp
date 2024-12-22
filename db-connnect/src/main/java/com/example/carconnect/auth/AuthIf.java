package com.example.carconnect.auth;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Schnittstelle für Authentifizierungsoperationen, die über RMI (Remote Method Invocation) verfügbar sind.
 * Diese Schnittstelle definiert Methoden zur Verwaltung von Benutzersitzungen und -anmeldungen.
 */
public interface AuthIf extends Remote {

    long generateNewSessionId(String username) throws RemoteException;
    boolean validateUser(long sessionId, String hash) throws RemoteException;
    boolean registerUser(String username, String hash) throws RemoteException;
    boolean changePassword(String username, String oldPassword, String newPassword) throws RemoteException;
}
