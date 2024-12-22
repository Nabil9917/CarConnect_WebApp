package com.example.carconnect.booking;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * Servlet zur Verwaltung von Buchungsanfragen eines bestimmten Benutzers.
 * Dieses Servlet kommuniziert über RMI mit dem Buchungsdienst, um die Buchungsanfragen abzurufen und als JSON zurückzugeben.
 *
 */
@WebServlet(name = "BookingRequestsServlet", urlPatterns = {"/bookingRequests"})
public class BookingRequestsServlet extends HttpServlet {
    private BookingIf bookingIf;

    @Override
    public void init() throws ServletException {
        try {
            bookingIf = (BookingIf) Naming.lookup("rmi://localhost:1099/BookingIf");
        } catch (Exception e) {
            throw new ServletException("Failed to lookup RMI server", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        System.out.println("Received username: " + username);
        JSONArray bookingRequests = new JSONArray();
        try {
            List<Map<String, Object>> resultSet = bookingIf.getBookingRequests(username);
            for (Map<String, Object> result : resultSet) {
                JSONObject requestJson = new JSONObject();
                requestJson.put("id", result.get("id"));
                requestJson.put("vehicle", result.get("vehicle"));
                requestJson.put("requester", result.get("requester"));
                requestJson.put("start_time", result.get("start_time"));
                requestJson.put("end_time", result.get("end_time"));
                requestJson.put("status", result.get("status"));
                bookingRequests.put(requestJson);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("Booking Requests: " + bookingRequests.toString());
        response.setContentType("application/json");
        response.getWriter().write(bookingRequests.toString());
    }
}
