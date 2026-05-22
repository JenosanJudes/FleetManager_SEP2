package client.view;

import client.viewmodel.VehicleDetailViewModel;
import client.viewmodel.VehicleListViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import shared.domain.*;

// Controller for detaljeskærmen - viser og redigerer én bil
public class VehicleDetailController {

    // Informationsfelter
    @FXML private TextField   licensePlateField;
    @FXML private TextField   brandField;
    @FXML private TextField   modelField;
    @FXML private TextField   ownershipField;
    @FXML private TextField   agreementNoField;
    @FXML private TextField   maxKmField;
    @FXML private ComboBox<VehicleType>   vehicleTypeBox;
    @FXML private ComboBox<PlateColor>    plateColorBox;
    @FXML private ComboBox<LeasingType>   leasingTypeBox;
    @FXML private ComboBox<VehicleStatus> statusBox;
    @FXML private ComboBox<Department>    departmentBox;
    @FXML private DatePicker  deliveryDatePicker;
    @FXML private DatePicker  leasingStartPicker;
    @FXML private DatePicker  leasingEndPicker;
    @FXML private DatePicker  expiryDatePicker;

    // Bilfører-felter
    @FXML private Label driverNameLabel;
    @FXML private Label driverEmailLabel;
    @FXML private Label driverPhoneLabel;
    @FXML private Label driverDeptLabel;
    @FXML private ComboBox<Employee> assignEmployeeBox;

    @FXML private Label statusLabel;

    private final VehicleDetailViewModel viewModel = new VehicleDetailViewModel();
    private VehicleListViewModel listViewModel;
    private Stage stage;

    // Kaldes fra VehicleListController
    public void setup(Vehicle vehicle, VehicleListViewModel listViewModel, Stage stage) {
        this.listViewModel = listViewModel;
        this.stage = stage;

        // Fyld dropdowns
        vehicleTypeBox.getItems().setAll(VehicleType.values());
        plateColorBox.getItems().setAll(PlateColor.values());
        leasingTypeBox.getItems().setAll(LeasingType.values());
        statusBox.getItems().setAll(VehicleStatus.values());

        // Bind alle felter til viewmodellen
        licensePlateField.textProperty().bindBidirectional(viewModel.licensePlateProperty());
        brandField.textProperty().bindBidirectional(viewModel.brandProperty());
        modelField.textProperty().bindBidirectional(viewModel.modelProperty());
        ownershipField.textProperty().bindBidirectional(viewModel.ownershipProperty());
        agreementNoField.textProperty().bindBidirectional(viewModel.agreementNoProperty());
        vehicleTypeBox.valueProperty().bindBidirectional(viewModel.vehicleTypeProperty());
        plateColorBox.valueProperty().bindBidirectional(viewModel.plateColorProperty());
        leasingTypeBox.valueProperty().bindBidirectional(viewModel.leasingTypeProperty());
        statusBox.valueProperty().bindBidirectional(viewModel.statusProperty());
        departmentBox.valueProperty().bindBidirectional(viewModel.departmentProperty());
        deliveryDatePicker.valueProperty().bindBidirectional(viewModel.deliveryDateProperty());
        leasingStartPicker.valueProperty().bindBidirectional(viewModel.leasingStartProperty());
        leasingEndPicker.valueProperty().bindBidirectional(viewModel.leasingEndProperty());
        expiryDatePicker.valueProperty().bindBidirectional(viewModel.expiryDateProperty());

        // Bind bilfører-labels
        driverNameLabel.textProperty().bind(viewModel.driverNameProperty());
        driverEmailLabel.textProperty().bind(viewModel.driverEmailProperty());
        driverPhoneLabel.textProperty().bind(viewModel.driverPhoneProperty());
        driverDeptLabel.textProperty().bind(viewModel.driverDeptProperty());
        statusLabel.textProperty().bind(viewModel.statusMessageProperty());

        // Bind afdelings-dropdown
        departmentBox.setItems(viewModel.getDepartments());
        assignEmployeeBox.setItems(viewModel.getEmployees());

        // Hent afdelinger og medarbejdere
        viewModel.loadDepartments();
        viewModel.loadEmployees();

        // Indlæs bil-data hvis vi redigerer en eksisterende bil
        if (vehicle != null) {
            viewModel.loadVehicle(vehicle);
        }

        // Sæt afdelings-valget efter data er indlæst
        if (vehicle != null && vehicle.departmentId() != null) {
            viewModel.getDepartments().addListener((javafx.collections.ListChangeListener<Department>) c -> {
                viewModel.getDepartments().stream()
                        .filter(d -> d.id() == vehicle.departmentId())
                        .findFirst()
                        .ifPresent(d -> viewModel.departmentProperty().set(d));
            });
        }
    }

    @FXML
    public void onSaveClick() {
        Vehicle vehicle = viewModel.buildVehicle();
        listViewModel.saveVehicle(vehicle);
        stage.close();
    }

    @FXML
    public void onAssignClick() {
        Employee selected = assignEmployeeBox.getValue();
        if (selected == null) {
            viewModel.statusMessageProperty().set("Vælg en medarbejder først");
            return;
        }
        viewModel.assignEmployee(selected, () -> {
            assignEmployeeBox.setValue(null);
        });
    }

    @FXML
    public void onCancelClick() {
        stage.close();
    }
}
