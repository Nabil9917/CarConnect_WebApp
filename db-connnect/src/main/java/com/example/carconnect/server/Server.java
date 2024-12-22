package com.example.carconnect.server;

import com.example.carconnect.auth.AuthIf;
import com.example.carconnect.auth.AuthImpl;
import com.example.carconnect.booking.BookingIf;
import com.example.carconnect.booking.BookingImpl;
import com.example.carconnect.repository.DBConnectionManager;
import com.example.carconnect.repository.UserRepository;
import com.example.carconnect.repository.VehicleRepository;
import com.example.carconnect.repository.BookingRepository;
import com.example.carconnect.vehicle.VehicleIf;
import com.example.carconnect.vehicle.VehicleImpl;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Hauptklasse für den RMI-Server, die die verschiedenen Services für die Authentifizierung,
 * Fahrzeugverwaltung und Buchungen bereitstellt. Diese Klasse startet das RMI-Registry,
 * initialisiert die notwendigen Repositories und bindet die Services an die RMI-Registry.
 *
 */
public class Server {


    private static final int PORT = 1099;


    public static void main(String[] args) {
        try {

            Registry registry;
            try {
                registry = LocateRegistry.getRegistry(PORT);
                registry.list();
            } catch (RemoteException e) {
                registry = LocateRegistry.createRegistry(PORT);
            }

            DBConnectionManager dbConnectionManager = new DBConnectionManager();
            UserRepository userRepository = new UserRepository(dbConnectionManager);
            VehicleRepository vehicleRepository = new VehicleRepository(dbConnectionManager);
            BookingRepository bookingRepository = new BookingRepository(dbConnectionManager);
            AuthIf authService = new AuthImpl(userRepository);
            Naming.rebind("rmi://localhost:" + PORT + "/AuthIf", authService);

            VehicleIf vehicleService = new VehicleImpl(vehicleRepository);
            Naming.rebind("rmi://localhost:" + PORT + "/VehicleIf", vehicleService);

            BookingIf bookingService = new BookingImpl(bookingRepository, vehicleRepository);
            Naming.rebind("rmi://localhost:" + PORT + "/BookingIf", bookingService);

            System.out.println("Server is ready on port " + PORT);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
