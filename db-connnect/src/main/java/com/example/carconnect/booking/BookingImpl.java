package com.example.carconnect.booking;

import com.example.carconnect.repository.BookingRepository;
import com.example.carconnect.repository.VehicleRepository;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

/**
 * Implementierung der Buchungslogik, die die `BookingIf`-Schnittstelle über RMI bereitstellt.
 * Diese Klasse bietet Methoden zur Verwaltung von Fahrzeugbuchungen, Buchungsanfragen,
 * Genehmigungen, Ablehnungen und zur Aufzeichnung der Fahrzeugnutzung.
 */
public class BookingImpl extends UnicastRemoteObject implements BookingIf {

    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;


    public BookingImpl(BookingRepository bookingRepository, VehicleRepository vehicleRepository) throws RemoteException {
        super();
        this.bookingRepository = bookingRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public boolean bookVehicle(String username, int vehicleId, String startTime, String endTime) throws RemoteException {
        try {
            boolean result = bookingRepository.bookVehicle(username, vehicleId, startTime, endTime);
            if (result) {
                bookingRepository.recordUsage(username, vehicleId, startTime, endTime);
                System.out.println("Booking result for vehicle " + vehicleId + ": " + result);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteException("Error booking vehicle: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean insertBookingRequest(String username, int vehicleId, String startTime, String endTime) throws RemoteException {
        return bookingRepository.insertBookingRequest(username, vehicleId, startTime, endTime);
    }


    @Override
    public boolean approveBookingRequest(int requestId) throws RemoteException {
        boolean result = bookingRepository.updateBookingRequestStatus(requestId, "APPROVED");
        if (result) {
            int vehicleId = bookingRepository.getVehicleIdFromBookingRequest(requestId);
            vehicleRepository.updateVehicleAvailability(vehicleId, false);
        }
        return result;
    }

    @Override
    public boolean rejectBookingRequest(int requestId) throws RemoteException {
        return bookingRepository.updateBookingRequestStatus(requestId, "REJECTED");
    }

    @Override
    public List<Map<String, Object>> getBookingRequests(String username) throws RemoteException {
        return bookingRepository.getBookingRequests(username);
    }

    @Override
    public int getVehicleIdFromBookingRequest(int requestId) throws RemoteException {
        return bookingRepository.getVehicleIdFromBookingRequest(requestId);
    }

    @Override
    public boolean recordUsage(String username, int vehicleId, String startTime, String endTime) throws RemoteException {
        return bookingRepository.recordUsage(username, vehicleId, startTime, endTime);
    }

    @Override
    public List<Map<String, Object>> getUsageHistory(String username) throws RemoteException {
        return bookingRepository.getUsageHistory(username);
    }
}
