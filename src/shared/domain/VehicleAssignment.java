package shared.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

// Kobling mellem en bil og den medarbejder der kører i den
public record VehicleAssignment(
        int id,
        int vehicleId,
        String licensePlate,
        int employeeId,
        String employeeName,
        String employeeEmail,
        String employeePhone,
        String departmentName,
        LocalDateTime assignedAt,
        LocalDateTime unassignedAt   // null = tildeling er stadig aktiv
) implements Serializable {
}
