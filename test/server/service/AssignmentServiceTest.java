package server.service;

import org.junit.jupiter.api.*;
import server.persistence.AssignmentDao;
import shared.domain.VehicleAssignment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Tests for AssignmentService.
// Vi bruger en fake DAO i stedet for databasen, så vi ikke behøver PostgreSQL.
// Jeg bruger Triple A (Arrange, Act, Assert) i alle tests.
class AssignmentServiceTest {

    // Fake DAO der gemmer tildelinger i en liste
    private static class InMemoryAssignmentDao implements AssignmentDao {

        private final List<VehicleAssignment> database = new ArrayList<>();
        private int nextId = 1;

        @Override
        public VehicleAssignment getActiveForVehicle(int vehicleId) {
            // Find den aktive tildeling (unassignedAt er null = stadig aktiv)
            for (VehicleAssignment a : database) {
                if (a.vehicleId() == vehicleId && a.unassignedAt() == null) return a;
            }
            return null;
        }

        @Override
        public VehicleAssignment assign(int vehicleId, int employeeId) {
            // Afslut eventuel eksisterende tildeling for bilen
            for (int i = 0; i < database.size(); i++) {
                VehicleAssignment a = database.get(i);
                if (a.vehicleId() == vehicleId && a.unassignedAt() == null) {
                    database.set(i, new VehicleAssignment(
                            a.id(), a.vehicleId(), a.licensePlate(),
                            a.employeeId(), a.employeeName(), a.employeeEmail(),
                            a.employeePhone(), a.departmentName(),
                            a.assignedAt(), LocalDateTime.now()
                    ));
                }
            }
            // Opret ny tildeling
            VehicleAssignment saved = new VehicleAssignment(
                    nextId++, vehicleId, "TEST01",
                    employeeId, "Testperson", "test@via.dk",
                    "12345678", "IT",
                    LocalDateTime.now(), null
            );
            database.add(saved);
            return saved;
        }

        @Override
        public void unassign(int vehicleId) {
            // Sæt unassignedAt til nu på den aktive tildeling
            for (int i = 0; i < database.size(); i++) {
                VehicleAssignment a = database.get(i);
                if (a.vehicleId() == vehicleId && a.unassignedAt() == null) {
                    database.set(i, new VehicleAssignment(
                            a.id(), a.vehicleId(), a.licensePlate(),
                            a.employeeId(), a.employeeName(), a.employeeEmail(),
                            a.employeePhone(), a.departmentName(),
                            a.assignedAt(), LocalDateTime.now()
                    ));
                }
            }
        }
    }

    private AssignmentService service;

    @BeforeEach
    void setUp() {
        // Kører før hver test - nulstiller alt
        service = new AssignmentService(new InMemoryAssignmentDao());
    }


    // --- Tests for assign() - grænseværdier (BVA) ---

    @Test
    @DisplayName("EP1 – assign med gyldige ids returnerer aktiv tildeling")
    void assign_withValidIds_returnsAssignment() {
        // Arrange - gyldige ids er større end 0

        // Act
        VehicleAssignment result = service.assign(1, 1);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.vehicleId());
        assertEquals(1, result.employeeId());
        assertNull(result.unassignedAt(), "Ny tildeling skal have unassignedAt = null");
    }

    @Test
    @DisplayName("BVA – assign med vehicleId = 0 kaster IllegalArgumentException")
    void assign_withZeroVehicleId_throwsIllegalArgumentException() {
        // Arrange - 0 er grænsen, det er ikke gyldigt

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.assign(0, 1));
    }

    @Test
    @DisplayName("EP2 – assign med negativt vehicleId kaster IllegalArgumentException")
    void assign_withNegativeVehicleId_throwsIllegalArgumentException() {
        // Arrange

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.assign(-1, 1));
    }

    @Test
    @DisplayName("BVA – assign med employeeId = 0 kaster IllegalArgumentException")
    void assign_withZeroEmployeeId_throwsIllegalArgumentException() {
        // Arrange - 0 er grænsen, det er ikke gyldigt

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.assign(1, 0));
    }


    // --- Tests for unassign() ---

    @Test
    @DisplayName("EP1 – unassign med gyldigt vehicleId kaster ingen exception")
    void unassign_withValidVehicleId_doesNotThrow() {
        // Arrange
        service.assign(1, 1);

        // Act & Assert
        assertDoesNotThrow(() -> service.unassign(1));
    }

    @Test
    @DisplayName("BVA – unassign med vehicleId = 0 kaster IllegalArgumentException")
    void unassign_withZeroVehicleId_throwsIllegalArgumentException() {
        // Arrange - 0 er grænsen, det er ikke gyldigt

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.unassign(0));
    }

    @Test
    @DisplayName("EP2 – unassign med negativt vehicleId kaster IllegalArgumentException")
    void unassign_withNegativeVehicleId_throwsIllegalArgumentException() {
        // Arrange

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.unassign(-5));
    }
}
