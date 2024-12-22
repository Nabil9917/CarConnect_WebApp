package com.example.carconnect.auth;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Schnittstelle für Authentifizierungsoperationen, die über RMI (Remote Method Invocation) verfügbar sind.
 * Diese Schnittstelle ermöglicht die Verwaltung von Benutzersitzungen, die Validierung von Benutzern,
 * die Registrierung neuer Benutzer und die Änderung von Benutzerpasswörtern.
 */
public interface AuthIf extends Remote {
    long generateNewSessionId(String username) throws RemoteException;
    boolean validateUser(long sessionId, String hash) throws RemoteException;
    boolean registerUser(String username, String hash) throws RemoteException;
    boolean changePassword(String username, String oldPassword, String newPassword) throws RemoteException;
}
