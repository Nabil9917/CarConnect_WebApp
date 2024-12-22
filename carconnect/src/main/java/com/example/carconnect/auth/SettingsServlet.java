package com.example.carconnect.auth;

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
 * Servlet zur Verwaltung von Benutzereinstellungen, speziell zum Ändern des Passworts.
 * Dieses Servlet kommuniziert über RMI mit dem Authentifizierungsdienst, um Passwortänderungen durchzuführen.
 */
@WebServlet(name = "SettingsServlet", urlPatterns = {"/changePassword"})
public class SettingsServlet extends HttpServlet {
    private AuthIf authIf;


    @Override
    public void init() throws ServletException {
        try {
            authIf = (AuthIf) Naming.lookup("rmi://localhost:1099/AuthIf");
        } catch (Exception e) {
            throw new ServletException("Failed to lookup RMI server", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        JSONObject jsonResponse = new JSONObject();

        try {
            boolean result = authIf.changePassword(username, oldPassword, newPassword);
            jsonResponse.put("success", result);
            if (!result) {
                jsonResponse.put("message", "Password change failed. Old password may be incorrect.");
            }
        } catch (RemoteException e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", e.getMessage());
        }

        response.setContentType("application/json");
        response.getWriter().write(jsonResponse.toString());
    }
}
