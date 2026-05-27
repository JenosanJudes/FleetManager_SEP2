package server.service;

import server.persistence.AssignmentDao;
import shared.domain.VehicleAssignment;

// Forretningslogik for biltildelinger - sidder imellem netværk og database
public class AssignmentService {

    private final AssignmentDao assignmentDao;

    public AssignmentService(AssignmentDao assignmentDao) {
        this.assignmentDao = assignmentDao;
    }

    public VehicleAssignment assign(int vehicleId, int employeeId) {
        if (vehicleId <= 0 || employeeId <= 0) {
            throw new IllegalArgumentException("Ugyldigt bil-id eller medarbejder-id");
        }
        return assignmentDao.assign(vehicleId, employeeId);
    }

    public void unassign(int vehicleId) {
        if (vehicleId <= 0) {
            throw new IllegalArgumentException("Ugyldigt bil-id");
        }
        assignmentDao.unassign(vehicleId);
    }

    public VehicleAssignment getActiveForVehicle(int vehicleId) {
        return assignmentDao.getActiveForVehicle(vehicleId);
    }
}
