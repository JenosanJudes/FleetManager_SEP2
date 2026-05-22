package server.service;

import server.persistence.VehicleDao;
import shared.domain.Vehicle;

import java.util.List;

// Forretningslogik for biler - sidder imellem netværk og database
public class VehicleService {

    private final VehicleDao vehicleDao;

    public VehicleService(VehicleDao vehicleDao) {
        this.vehicleDao = vehicleDao;
    }

    public List<Vehicle> getAll() {
        return vehicleDao.getAll();
    }

    public Vehicle getById(int id) {
        return vehicleDao.getById(id);
    }

    public Vehicle create(Vehicle vehicle) {
        if (vehicle.licensePlate() == null || vehicle.licensePlate().isBlank()) {
            throw new IllegalArgumentException("Reg.nr. må ikke være tomt");
        }
        return vehicleDao.create(vehicle);
    }

    public Vehicle update(Vehicle vehicle) {
        if (vehicle.licensePlate() == null || vehicle.licensePlate().isBlank()) {
            throw new IllegalArgumentException("Reg.nr. må ikke være tomt");
        }
        return vehicleDao.update(vehicle);
    }

    public List<Vehicle> search(String licensePlate) {
        if (licensePlate == null || licensePlate.isBlank()) {
            return getAll();
        }
        return vehicleDao.search(licensePlate.trim());
    }
}
