package com.example.carconnect.vehicle;

import com.example.carconnect.repository.VehicleRepository;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;

/**
 * Implementierung der `VehicleIf`-Schnittstelle, die über RMI verfügbar ist.
 * Diese Klasse stellt die Methoden zur Verwaltung von Fahrzeugen bereit und
 * verwendet das `VehicleRepository` für den Zugriff auf die Datenbank.
 */
public class VehicleImpl extends UnicastRemoteObject implements VehicleIf {

    private final VehicleRepository vehicleRepository;

    public VehicleImpl(VehicleRepository vehicleRepository) throws RemoteException {
        super();
        this.vehicleRepository = vehicleRepository;
    }


    @Override
    public boolean registerVehicle(String ownerUsername, String make, String model, int year, String location) throws RemoteException {
        return vehicleRepository.registerVehicle(ownerUsername, make, model, year, location);
    }


    @Override
    public List<Map<String, Object>> searchAvailableVehicles(Map<String, String> searchCriteria) throws RemoteException {
        return vehicleRepository.searchAvailableVehicles(searchCriteria);
    }

    @Override
    public boolean updateVehicle(int vehicleId, String make, String model, int year, String location) throws RemoteException {
        return vehicleRepository.updateVehicle(vehicleId, make, model, year, location);
    }


    @Override
    public boolean deleteVehicle(int vehicleId) throws RemoteException {
        return vehicleRepository.deleteVehicle(vehicleId);
    }

    @Override
    public boolean isVehicleOwner(String username, int vehicleId) throws RemoteException {
        boolean isOwner = vehicleRepository.isVehicleOwner(username, vehicleId);
        System.out.println("AuthServerImpl.isVehicleOwner: " + isOwner + " for vehicleId: " + vehicleId + " and username: " + username);
        return isOwner;
    }


    @Override
    public int getLastInsertedVehicleId() throws RemoteException {
        return vehicleRepository.getLastInsertedVehicleId();
    }
}
