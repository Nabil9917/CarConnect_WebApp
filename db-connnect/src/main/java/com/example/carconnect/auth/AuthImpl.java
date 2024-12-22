package com.example.carconnect.auth;

import com.example.carconnect.repository.UserRepository;

import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementierung der Authentifizierungs-Logik, die die AuthIf-Schnittstelle über RMI bereitstellt.
 * Diese Klasse bietet Methoden zur Sitzungsverwaltung, Benutzervalidierung, Registrierung und Passwortänderung.
 */
public class AuthImpl extends UnicastRemoteObject implements AuthIf {

    private final UserRepository userRepository;
    private Map<Long, String> sessionToUserMap = new HashMap<>();


    public AuthImpl(UserRepository userRepository) throws RemoteException {
        super();
        this.userRepository = userRepository;
    }

    @Override
    public long generateNewSessionId(String username) {
        for (Map.Entry<Long, String> entry : sessionToUserMap.entrySet()) {
            if (entry.getValue().equals(username)) {
                return entry.getKey();
            }
        }
        long newSessionId = new Date().getTime();
        sessionToUserMap.put(newSessionId, username);
        return newSessionId;
    }

    @Override
    public boolean validateUser(long sessionId, String hash) {
        String username = sessionToUserMap.get(sessionId);
        System.out.println("Validating user: " + username);
        if (username == null) {
            System.out.println("Username not found for session ID: " + sessionId);
            return false;
        }
        if (!userRepository.checkUserExistence(username)) {
            System.out.println("User does not exist: " + username);
            return false;
        }
        String userHash = userRepository.getHash(username);
        if (userHash == null) {
            System.out.println("Hash not found for user: " + username);
            return false;
        }
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digester.digest((sessionId + userHash).getBytes(StandardCharsets.UTF_8));
            String newHash = bytesToHex(encodedHash);
            sessionToUserMap.remove(sessionId);
            System.out.println("Expected hash: " + newHash + ", Received hash: " + hash);
            return newHash.equals(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean registerUser(String username, String hash) {
        System.out.println("Registering user: " + username);
        boolean userExists = userRepository.checkUserExistence(username);
        System.out.println("User exists: " + userExists);
        if (userExists) {
            return false;
        }
        boolean registrationResult = userRepository.registerNewUser(username, hash);
        System.out.println("Registration result: " + registrationResult);
        return registrationResult;
    }

    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        try {
            String currentHash = userRepository.getHash(username);
            MessageDigest digester = MessageDigest.getInstance("SHA-256");
            byte[] oldPasswordHash = digester.digest(oldPassword.getBytes());
            if (!currentHash.equals(bytesToHex(oldPasswordHash))) {
                return false;
            }
            byte[] newPasswordHash = digester.digest(newPassword.getBytes());
            return userRepository.updateUserPassword(username, bytesToHex(newPasswordHash));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
