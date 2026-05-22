package shared.domain;

import java.io.Serializable;

// Medarbejder - kan være bilfører eller bilansvarlig
public record Employee(
        int id,
        String employeeNo,
        String fullName,
        String email,
        String phone,
        int departmentId,
        String departmentName,
        EmployeeRole role,
        EmployeeStatus status
) implements Serializable {

    @Override
    public String toString() {
        return fullName;
    }
}
