package client.view;

import client.viewmodel.VehicleListViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shared.domain.Vehicle;

// Controller for bil-listen
public class VehicleListController {

    @FXML private TableView<Vehicle>           vehicleTable;
    @FXML private TableColumn<Vehicle, String> colLicensePlate;
    @FXML private TableColumn<Vehicle, String> colBrand;
    @FXML private TableColumn<Vehicle, String> colModel;
    @FXML private TableColumn<Vehicle, String> colType;
    @FXML private TableColumn<Vehicle, String> colOwnership;
    @FXML private TableColumn<Vehicle, String> colStatus;
    @FXML private TableColumn<Vehicle, String> colDepartment;
    @FXML private TextField                    searchField;
    @FXML private Label                        statusLabel;

    private final VehicleListViewModel viewModel = new VehicleListViewModel();

    @FXML
    public void initialize() {
        // Kobl kolonner til Vehicle-felterne (records har ikke getLicensePlate(), så vi bruger lambda)
        colLicensePlate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().licensePlate()));
        colBrand.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().brand()));
        colModel.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().model()));
        colType.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().vehicleType().name()));
        colOwnership.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().ownership()));
        colStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().status().name()));
        colDepartment.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().departmentName()));

        // Bind søgefeltet og statusbeskeden
        searchField.textProperty().bindBidirectional(viewModel.searchTextProperty());
        statusLabel.textProperty().bind(viewModel.statusMessageProperty());

        // Bind tabellen til listen i viewmodellen
        vehicleTable.setItems(viewModel.getVehicles());

        // Dobbeltklik åbner detaljevisning
        vehicleTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
                if (selected != null) openDetailWindow(selected);
            }
        });

        // Hent biler med det samme
        viewModel.loadVehicles();
    }

    @FXML
    public void onSearchClick() {
        viewModel.search();
    }

    @FXML
    public void onNewVehicleClick() {
        openDetailWindow(null);
    }

    // Åbner detail-vinduet for en valgt bil (eller null for ny bil)
    private void openDetailWindow(Vehicle vehicle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/view/vehicle-detail.fxml"));
            Stage detailStage = new Stage();
            detailStage.initModality(Modality.APPLICATION_MODAL);
            detailStage.setTitle(vehicle == null ? "Ny bil" : vehicle.licensePlate());
            detailStage.setScene(new Scene(loader.load(), 850, 650));

            VehicleDetailController controller = loader.getController();
            controller.setup(vehicle, viewModel, detailStage);

            detailStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
