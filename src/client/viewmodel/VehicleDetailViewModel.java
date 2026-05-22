package client.viewmodel;

import client.AppContext;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.domain.*;
import shared.protocol.Request;
import shared.protocol.RequestType;
import shared.protocol.Response;

import java.time.LocalDate;
import java.util.List;

// ViewModel for detaljevisningen af en bil
public class VehicleDetailViewModel {

    // Alle felter for bilen
    private final StringProperty licensePlate  = new SimpleStringProperty("");
    private final StringProperty brand         = new SimpleStringProperty("");
    private final StringProperty model         = new SimpleStringProperty("");
    private final StringProperty ownership     = new SimpleStringProperty("");
    private final StringProperty agreementNo   = new SimpleStringProperty("");
    private final ObjectProperty<VehicleType>  vehicleType  = new SimpleObjectProperty<>(VehicleType.PERSONBIL);
    private final ObjectProperty<PlateColor>   plateColor   = new SimpleObjectProperty<>(PlateColor.HVID);
    private final ObjectProperty<LeasingType>  leasingType  = new SimpleObjectProperty<>(LeasingType.OPERATIONEL);
    private final ObjectProperty<VehicleStatus> status      = new SimpleObjectProperty<>(VehicleStatus.AKTIV);
    private final ObjectProperty<LocalDate>    deliveryDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate>    leasingStart = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate>    leasingEnd   = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate>    expiryDate   = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer>      maxKm        = new SimpleObjectProperty<>();
    private final ObjectProperty<Department>   department   = new SimpleObjectProperty<>();

    // Bilfører-info
    private final StringProperty driverName    = new SimpleStringProperty("-");
    private final StringProperty driverEmail   = new SimpleStringProperty("-");
    private final StringProperty driverPhone   = new SimpleStringProperty("-");
    private final StringProperty driverDept    = new SimpleStringProperty("-");

    private final ObservableList<Department> departments = FXCollections.observableArrayList();
    private final ObservableList<Employee>   employees   = FXCollections.observableArrayList();
    private final StringProperty statusMessage = new SimpleStringProperty("");

    private Vehicle currentVehicle;

    // Indlæser data for en specifik bil
    public void loadVehicle(Vehicle vehicle) {
        this.currentVehicle = vehicle;

        licensePlate.set(vehicle.licensePlate());
        brand.set(vehicle.brand() != null ? vehicle.brand() : "");
        model.set(vehicle.model() != null ? vehicle.model() : "");
        ownership.set(vehicle.ownership() != null ? vehicle.ownership() : "");
        agreementNo.set(vehicle.agreementNo() != null ? vehicle.agreementNo() : "");
        vehicleType.set(vehicle.vehicleType());
        plateColor.set(vehicle.plateColor());
        leasingType.set(vehicle.leasingType() != null ? vehicle.leasingType() : LeasingType.OPERATIONEL);
        status.set(vehicle.status());
        deliveryDate.set(vehicle.deliveryDate());
        leasingStart.set(vehicle.leasingStart());
        leasingEnd.set(vehicle.leasingEnd());
        expiryDate.set(vehicle.expiryDate());
        maxKm.set(vehicle.maxKm());

        // Hent aktiv bilfører
        loadAssignment(vehicle.id());
    }

    // Henter hvem der kører i bilen
    private void loadAssignment(int vehicleId) {
        new Thread(() -> {
            try {
                Request request = new Request(RequestType.GET_ASSIGNMENT_FOR_VEHICLE, vehicleId);
                Response response = AppContext.getConnection().send(request);

                if (response.isSuccess() && response.getData() != null) {
                    VehicleAssignment a = (VehicleAssignment) response.getData();
                    Platform.runLater(() -> {
                        driverName.set(a.employeeName());
                        driverEmail.set(a.employeeEmail() != null ? a.employeeEmail() : "-");
                        driverPhone.set(a.employeePhone() != null ? a.employeePhone() : "-");
                        driverDept.set(a.departmentName() != null ? a.departmentName() : "-");
                    });
                } else {
                    Platform.runLater(() -> {
                        driverName.set("-");
                        driverEmail.set("-");
                        driverPhone.set("-");
                        driverDept.set("-");
                    });
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusMessage.set("Fejl: " + e.getMessage()));
            }
        }).start();
    }

    // Henter alle afdelinger til dropdowns
    public void loadDepartments() {
        new Thread(() -> {
            try {
                Request request = new Request(RequestType.GET_ALL_DEPARTMENTS, null);
                Response response = AppContext.getConnection().send(request);
                if (response.isSuccess()) {
                    List<Department> list = (List<Department>) response.getData();
                    Platform.runLater(() -> departments.setAll(list));
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusMessage.set("Fejl: " + e.getMessage()));
            }
        }).start();
    }

    // Henter alle medarbejdere til tildeling
    public void loadEmployees() {
        new Thread(() -> {
            try {
                Request request = new Request(RequestType.GET_ALL_EMPLOYEES, null);
                Response response = AppContext.getConnection().send(request);
                if (response.isSuccess()) {
                    List<Employee> list = (List<Employee>) response.getData();
                    Platform.runLater(() -> employees.setAll(list));
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusMessage.set("Fejl: " + e.getMessage()));
            }
        }).start();
    }

    // Tildeler en medarbejder til bilen
    public void assignEmployee(Employee employee, Runnable onSuccess) {
        new Thread(() -> {
            try {
                int[] ids = { currentVehicle.id(), employee.id() };
                Request request = new Request(RequestType.ASSIGN_VEHICLE, ids);
                Response response = AppContext.getConnection().send(request);

                if (response.isSuccess()) {
                    loadAssignment(currentVehicle.id());
                    Platform.runLater(onSuccess);
                } else {
                    Platform.runLater(() -> statusMessage.set(response.getMessage()));
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusMessage.set("Fejl: " + e.getMessage()));
            }
        }).start();
    }

    // Bygger et Vehicle-objekt ud fra de nuværende feltværdier
    public Vehicle buildVehicle() {
        Integer deptId = department.get() != null ? department.get().id() : null;
        String deptName = department.get() != null ? department.get().name() : null;
        int id = currentVehicle != null ? currentVehicle.id() : 0;

        return new Vehicle(
                id,
                licensePlate.get().trim().toUpperCase(),
                brand.get().trim(),
                model.get().trim(),
                vehicleType.get(),
                plateColor.get(),
                ownership.get().trim(),
                leasingType.get(),
                agreementNo.get().trim(),
                maxKm.get(),
                deliveryDate.get(),
                leasingStart.get(),
                leasingEnd.get(),
                expiryDate.get(),
                deptId,
                deptName,
                status.get()
        );
    }

    // Getters for alle properties
    public StringProperty licensePlateProperty()  { return licensePlate; }
    public StringProperty brandProperty()         { return brand; }
    public StringProperty modelProperty()         { return model; }
    public StringProperty ownershipProperty()     { return ownership; }
    public StringProperty agreementNoProperty()   { return agreementNo; }
    public ObjectProperty<VehicleType>   vehicleTypeProperty()  { return vehicleType; }
    public ObjectProperty<PlateColor>    plateColorProperty()   { return plateColor; }
    public ObjectProperty<LeasingType>   leasingTypeProperty()  { return leasingType; }
    public ObjectProperty<VehicleStatus> statusProperty()       { return status; }
    public ObjectProperty<LocalDate>     deliveryDateProperty() { return deliveryDate; }
    public ObjectProperty<LocalDate>     leasingStartProperty() { return leasingStart; }
    public ObjectProperty<LocalDate>     leasingEndProperty()   { return leasingEnd; }
    public ObjectProperty<LocalDate>     expiryDateProperty()   { return expiryDate; }
    public ObjectProperty<Integer>       maxKmProperty()        { return maxKm; }
    public ObjectProperty<Department>    departmentProperty()   { return department; }
    public StringProperty driverNameProperty()    { return driverName; }
    public StringProperty driverEmailProperty()   { return driverEmail; }
    public StringProperty driverPhoneProperty()   { return driverPhone; }
    public StringProperty driverDeptProperty()    { return driverDept; }
    public ObservableList<Department> getDepartments() { return departments; }
    public ObservableList<Employee>   getEmployees()   { return employees; }
    public StringProperty statusMessageProperty() { return statusMessage; }
}
