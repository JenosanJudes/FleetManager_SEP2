package server.service;

import org.junit.jupiter.api.*;
import server.persistence.VehicleDao;
import shared.domain.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Tests for VehicleService.
// Vi bruger en fake DAO (InMemoryVehicleDao) i stedet for den rigtige database,
// så vi kan teste uden at have PostgreSQL kørende.
// Jeg bruger Triple A (Arrange, Act, Assert) i alle tests.
class VehicleServiceTest {

    // Fake DAO der gemmer biler i en liste i stedet for en database
    private static class InMemoryVehicleDao implements VehicleDao {

        private final List<Vehicle> database = new ArrayList<>();
        private int nextId = 1;

        @Override
        public List<Vehicle> getAll() {
            return new ArrayList<>(database);
        }

        @Override
        public Vehicle getById(int id) {
            for (Vehicle v : database) {
                if (v.id() == id) return v;
            }
            return null;
        }

        @Override
        public Vehicle create(Vehicle vehicle) {
            // Giv bilen et id og gem den
            Vehicle saved = new Vehicle(
                    nextId++,
                    vehicle.licensePlate(),
                    vehicle.brand(),
                    vehicle.model(),
                    vehicle.vehicleType(),
                    vehicle.plateColor(),
                    vehicle.ownership(),
                    vehicle.leasingType(),
                    vehicle.agreementNo(),
                    vehicle.maxKm(),
                    vehicle.deliveryDate(),
                    vehicle.leasingStart(),
                    vehicle.leasingEnd(),
                    vehicle.expiryDate(),
                    vehicle.departmentId(),
                    vehicle.departmentName(),
                    vehicle.status()
            );
            database.add(saved);
            return saved;
        }

        @Override
        public Vehicle update(Vehicle vehicle) {
            // Fjern den gamle og indsæt den nye
            database.removeIf(v -> v.id() == vehicle.id());
            database.add(vehicle);
            return vehicle;
        }

        @Override
        public List<Vehicle> search(String licensePlate) {
            List<Vehicle> result = new ArrayList<>();
            for (Vehicle v : database) {
                if (v.licensePlate().toLowerCase().contains(licensePlate.toLowerCase())) {
                    result.add(v);
                }
            }
            return result;
        }
    }

    private VehicleService service;
    private InMemoryVehicleDao stubDao;

    // Laver et minimalt Vehicle-objekt til brug i tests
    private Vehicle makeVehicle(int id, String licensePlate) {
        return new Vehicle(id, licensePlate, "Tesla", "Model Y",
                VehicleType.PERSONBIL, PlateColor.HVID,
                null, null, null, null,
                null, null, null, null,
                null, null, VehicleStatus.AKTIV);
    }

    @BeforeEach
    void setUp() {
        // Kører før hver test - nulstiller alt
        stubDao = new InMemoryVehicleDao();
        service = new VehicleService(stubDao);
    }


    // --- Tests for create() ---

    @Test
    @DisplayName("EP1 – create med gyldigt reg.nr. returnerer gemt bil")
    void create_withValidLicensePlate_returnsSavedVehicle() {
        // Arrange
        Vehicle vehicle = makeVehicle(0, "TEST01");

        // Act
        Vehicle result = service.create(vehicle);

        // Assert
        assertNotNull(result);
        assertEquals("TEST01", result.licensePlate());
        assertTrue(result.id() > 0, "Oprettet bil skal have et id > 0");
    }

    @Test
    @DisplayName("EP3 – create med tomt reg.nr. kaster IllegalArgumentException")
    void create_withEmptyLicensePlate_throwsIllegalArgumentException() {
        // Arrange
        Vehicle vehicle = makeVehicle(0, "");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.create(vehicle));
    }

    @Test
    @DisplayName("EP3 – create med null reg.nr. kaster IllegalArgumentException")
    void create_withNullLicensePlate_throwsIllegalArgumentException() {
        // Arrange
        Vehicle vehicle = makeVehicle(0, null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.create(vehicle));
    }

    @Test
    @DisplayName("EP3 – create med kun mellemrum kaster IllegalArgumentException")
    void create_withBlankLicensePlate_throwsIllegalArgumentException() {
        // Arrange
        Vehicle vehicle = makeVehicle(0, "   ");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.create(vehicle));
    }


    // --- Tests for search() - grænseværdier (BVA) ---

    @Test
    @DisplayName("BVA – search med null returnerer alle biler")
    void search_withNull_returnsAllVehicles() {
        // Arrange
        stubDao.create(makeVehicle(0, "DT73085"));

        // Act
        List<Vehicle> result = service.search(null);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("BVA – search med tom streng returnerer alle biler")
    void search_withEmptyString_returnsAllVehicles() {
        // Arrange
        stubDao.create(makeVehicle(0, "DT73085"));

        // Act
        List<Vehicle> result = service.search("");

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("BVA – search med kun mellemrum returnerer alle biler")
    void search_withBlankString_returnsAllVehicles() {
        // Arrange
        stubDao.create(makeVehicle(0, "DT73085"));

        // Act
        List<Vehicle> result = service.search("   ");

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("EP1 – search med del af reg.nr. returnerer matchende biler")
    void search_withPartialMatch_returnsMatchingVehicles() {
        // Arrange
        stubDao.create(makeVehicle(0, "DT73085"));
        stubDao.create(makeVehicle(0, "EC45760"));

        // Act
        List<Vehicle> result = service.search("DT");

        // Assert
        assertEquals(1, result.size());
        assertEquals("DT73085", result.get(0).licensePlate());
    }

    @Test
    @DisplayName("EP2 – search med ingen match returnerer tom liste")
    void search_withNoMatch_returnsEmptyList() {
        // Arrange
        stubDao.create(makeVehicle(0, "DT73085"));

        // Act
        List<Vehicle> result = service.search("XXXXXX");

        // Assert
        assertTrue(result.isEmpty());
    }


    // --- Tests for update() ---

    @Test
    @DisplayName("update med gyldigt reg.nr. returnerer opdateret bil")
    void update_withValidLicensePlate_returnsUpdatedVehicle() {
        // Arrange
        Vehicle original = stubDao.create(makeVehicle(0, "DT73085"));
        Vehicle updated  = makeVehicle(original.id(), "DT99999");

        // Act
        Vehicle result = service.update(updated);

        // Assert
        assertEquals("DT99999", result.licensePlate());
    }

    @Test
    @DisplayName("update med tomt reg.nr. kaster IllegalArgumentException")
    void update_withEmptyLicensePlate_throwsIllegalArgumentException() {
        // Arrange
        Vehicle original = stubDao.create(makeVehicle(0, "DT73085"));
        Vehicle invalid  = makeVehicle(original.id(), "");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> service.update(invalid));
    }
}
