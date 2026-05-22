package server.persistence;

import shared.domain.Vehicle;

import java.util.List;

// Interface for alt hvad vi kan gøre med biler i databasen
public interface VehicleDao {

    List<Vehicle> getAll();

    Vehicle getById(int id);

    Vehicle create(Vehicle vehicle);

    Vehicle update(Vehicle vehicle);

    List<Vehicle> search(String licensePlate);
}
