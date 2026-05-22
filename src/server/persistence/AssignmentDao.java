package server.persistence;

import shared.domain.VehicleAssignment;

public interface AssignmentDao {
    VehicleAssignment getActiveForVehicle(int vehicleId);
    VehicleAssignment assign(int vehicleId, int employeeId);
    void unassign(int vehicleId);
}
