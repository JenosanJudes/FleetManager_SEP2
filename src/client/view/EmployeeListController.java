package client.view;

import client.viewmodel.EmployeeListViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import shared.domain.Employee;

// Controller for medarbejder-listen
public class EmployeeListController {

    @FXML private TableView<Employee>           employeeTable;
    @FXML private TableColumn<Employee, String> colEmployeeNo;
    @FXML private TableColumn<Employee, String> colName;
    @FXML private TableColumn<Employee, String> colEmail;
    @FXML private TableColumn<Employee, String> colPhone;
    @FXML private TableColumn<Employee, String> colDepartment;
    @FXML private TableColumn<Employee, String> colRole;
    @FXML private TableColumn<Employee, String> colStatus;
    @FXML private TextField                     searchField;
    @FXML private Label                         statusLabel;

    private final EmployeeListViewModel viewModel = new EmployeeListViewModel();

    @FXML
    public void initialize() {
        // Kobl kolonner til Employee-felterne
        colEmployeeNo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().employeeNo()));
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().fullName()));
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().email()));
        colPhone.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().phone()));
        colDepartment.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().departmentName()));
        colRole.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().role().name()));
        colStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().status().name()));

        // Bind søgefelt og statusbesked
        searchField.textProperty().bindBidirectional(viewModel.searchTextProperty());
        statusLabel.textProperty().bind(viewModel.statusMessageProperty());

        // Bind tabellen til listen i viewmodellen
        employeeTable.setItems(viewModel.getEmployees());

        // Dobbeltklik åbner detaljevinduet
        employeeTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Employee selected = employeeTable.getSelectionModel().getSelectedItem();
                if (selected != null) openDetailWindow(selected);
            }
        });

        // Hent medarbejdere med det samme
        viewModel.loadEmployees();
    }

    @FXML
    public void onSearchClick() {
        viewModel.search();
    }

    @FXML
    public void onNewEmployeeClick() {
        openDetailWindow(null);
    }

    // Åbner detail-vinduet (null = ny medarbejder)
    private void openDetailWindow(Employee employee) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/view/employee-detail.fxml"));
            Stage detailStage = new Stage();
            detailStage.initModality(Modality.APPLICATION_MODAL);
            detailStage.setTitle(employee == null ? "Ny medarbejder" : employee.fullName());
            detailStage.setScene(new Scene(loader.load(), 500, 420));

            EmployeeDetailController controller = loader.getController();
            controller.setup(employee, viewModel, detailStage);

            detailStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
