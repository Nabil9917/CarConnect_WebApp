package com.example.carconnect.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket-Endpunkt zur Verwaltung von Benachrichtigungen. Dieser Endpunkt ermöglicht es dem Server,
 * Benachrichtigungen an alle verbundenen Clients zu senden.
 */
@ServerEndpoint("/notifications")
public class NotificationEndpoint {
    private static final Set<NotificationEndpoint> connections = new CopyOnWriteArraySet<>();
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        connections.add(this);
    }


    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message from session id: " + session.getId() + ": " + message);
    }


    @OnClose
    public void onClose(Session session) {
        connections.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Error on session id: " + session.getId() + ": " + throwable.getMessage());
    }


    public static void sendNotification(String message) {
        for (NotificationEndpoint endpoint : connections) {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote().sendText(message);
                    System.out.println("Sent message to session id: " + endpoint.session.getId() + ": " + message);
                } catch (IOException e) {
                    System.err.println("Failed to send message to session id: " + endpoint.session.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
