package server.service;

import org.junit.jupiter.api.*;
import server.persistence.EmployeeDao;
import shared.domain.Employee;
import shared.domain.EmployeeRole;
import shared.domain.EmployeeStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EmployeeService.
 *
 * Testteknikker brugt:
 *   - Triple A (Arrange-Act-Assert)
 *   - Equivalence Partitioning (EP)
 *   - Black-box (vi tester adfærd ud fra krav, ikke intern kode)
 *
 * Databasen erstattes af en simpel in-memory stub (InMemoryEmployeeDao),
 * så tests kan køre uden PostgreSQL.
 */
class EmployeeServiceTest {

    // ──────────────────────────────────────────────────────────
    // In-memory stub – erstatter den rigtige PostgreSQL-DAO
    // ──────────────────────────────────────────────────────────
    private static class InMemoryEmployeeDao implements EmployeeDao {

        private final List<Employee> database = new ArrayList<>();
        private int nextId = 1;

        @Override
        public List<Employee> getAll() {
            return new ArrayList<>(database);
        }

        @Override
        public Employee getById(int id) {
            for (Employee e : database) {
                if (e.id() == id) return e;
            }
            return null;
        }

        @Override
        public Employee create(Employee employee) {
            Employee saved = new Employee(
                    nextId++,
                    employee.employeeNo(),
                    employee.fullName(),
                    employee.email(),
                    employee.phone(),
                    employee.departmentId(),
                    employee.departmentName(),
                    employee.role(),
                    employee.status()
            );
            database.add(saved);
            return saved;
        }

        @Override
        public Employee update(Employee employee) {
            database.removeIf(e -> e.id() == employee.id());
            database.add(employee);
            return employee;
        }
    }

    // ──────────────────────────────────────────────────────────
    // Opsætning – kører før hver enkelt test
    // ──────────────────────────────────────────────────────────
    private EmployeeService service;
    private InMemoryEmployeeDao stubDao;

    // Hjælpemetode – laver et minimalt Employee-objekt til brug i tests
    private Employee makeEmployee(int id, String fullName) {
        return new Employee(id, "EMP001", fullName,
                "test@via.dk", "12345678",
                1, "IT",
                EmployeeRole.BILFORER, EmployeeStatus.AKTIV);
    }

    @BeforeEach
    void setUp() {
        // Arrange (fælles): opret service med in-memory stub i stedet for databasen
        stubDao = new InMemoryEmployeeDao();
        service = new EmployeeService(stubDao);
    }


    // ──────────────────────────────────────────────────────────
    // create() – EP og Black-box
    // ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("EP1 – create med gyldigt navn returnerer gemt medarbejder")
    void create_withValidName_returnsSavedEmployee() {
        // Arrange
        Employee employee = makeEmployee(0, "Jens Jensen");

        // Act
        Employee result = service.create(employee);

        // Assert
        assertNotNull(result);
        assertEquals("Jens Jensen", result.fullName());
        assertTrue(result.id() > 0, "Oprettet medarbejder skal have et id > 0");
    }

    @Test
    @DisplayName("EP3 – create med tomt navn kaster IllegalArgumentException")
    void create_withEmptyName_throwsIllegalArgumentException() {
        // Arrange
        Employee employee = makeEmployee(0, "");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.create(employee));
    }

    @Test
    @DisplayName("EP3 – create med null navn kaster IllegalArgumentException")
    void create_withNullName_throwsIllegalArgumentException() {
        // Arrange
        Employee employee = makeEmployee(0, null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.create(employee));
    }

    @Test
    @DisplayName("EP3 – create med kun mellemrum kaster IllegalArgumentException")
    void create_withBlankName_throwsIllegalArgumentException() {
        // Arrange
        Employee employee = makeEmployee(0, "   ");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.create(employee));
    }


    // ──────────────────────────────────────────────────────────
    // update() – Black-box
    // ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("update med gyldigt navn returnerer opdateret medarbejder")
    void update_withValidName_returnsUpdatedEmployee() {
        // Arrange
        Employee original = stubDao.create(makeEmployee(0, "Jens Jensen"));
        Employee updated  = makeEmployee(original.id(), "Maria Madsen");

        // Act
        Employee result = service.update(updated);

        // Assert
        assertEquals("Maria Madsen", result.fullName());
    }
}
