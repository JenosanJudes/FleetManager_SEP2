package client.view;

import client.AppContext;
import client.viewmodel.EmployeeListViewModel;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import shared.domain.*;
import shared.protocol.Request;
import shared.protocol.RequestType;
import shared.protocol.Response;

import java.util.List;

// Controller for opret/rediger medarbejder
public class EmployeeDetailController {

    @FXML private TextField  employeeNoField;
    @FXML private TextField  fullNameField;
    @FXML private TextField  emailField;
    @FXML private TextField  phoneField;
    @FXML private ComboBox<Department>     departmentBox;
    @FXML private ComboBox<EmployeeRole>   roleBox;
    @FXML private ComboBox<EmployeeStatus> statusBox;
    @FXML private Label      errorLabel;

    private Employee employee;
    private EmployeeListViewModel viewModel;
    private Stage stage;

    @FXML
    public void initialize() {
        // Fyld combo-boksene med gyldige værdier
        roleBox.getItems().addAll(EmployeeRole.values());
        statusBox.getItems().addAll(EmployeeStatus.values());

        // Hent afdelinger fra serveren
        new Thread(() -> {
            try {
                Request request = new Request(RequestType.GET_ALL_DEPARTMENTS, null);
                Response response = AppContext.getConnection().send(request);
                if (response.isSuccess()) {
                    List<Department> depts = (List<Department>) response.getData();
                    Platform.runLater(() -> departmentBox.getItems().addAll(depts));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Kaldes fra EmployeeListController med den valgte medarbejder (eller null)
    public void setup(Employee employee, EmployeeListViewModel viewModel, Stage stage) {
        this.employee  = employee;
        this.viewModel = viewModel;
        this.stage     = stage;

        if (employee != null) {
            // Udfyld felterne med eksisterende data
            employeeNoField.setText(employee.employeeNo());
            fullNameField.setText(employee.fullName());
            emailField.setText(employee.email());
            phoneField.setText(employee.phone());
            roleBox.setValue(employee.role());
            statusBox.setValue(employee.status());

            // Sæt afdeling når listen er loadet
            departmentBox.getItems().addListener((javafx.collections.ListChangeListener<Department>) c -> {
                departmentBox.getItems().stream()
                        .filter(d -> d.id() == employee.departmentId())
                        .findFirst()
                        .ifPresent(departmentBox::setValue);
            });
        } else {
            // Standard-værdier for ny medarbejder
            roleBox.setValue(EmployeeRole.BILFOERER);
            statusBox.setValue(EmployeeStatus.ANSAT);
        }
    }

    @FXML
    public void onSaveClick() {
        // Tjek at navn er udfyldt
        if (fullNameField.getText().isBlank()) {
            errorLabel.setText("Fuldt navn er påkrævet");
            return;
        }
        if (departmentBox.getValue() == null) {
            errorLabel.setText("Vælg en afdeling");
            return;
        }

        int id = employee != null ? employee.id() : 0;

        Employee toSave = new Employee(
                id,
                employeeNoField.getText().trim(),
                fullNameField.getText().trim(),
                emailField.getText().trim(),
                phoneField.getText().trim(),
                departmentBox.getValue().id(),
                departmentBox.getValue().name(),
                roleBox.getValue(),
                statusBox.getValue()
        );

        viewModel.saveEmployee(toSave);
        stage.close();
    }

    @FXML
    public void onCancelClick() {
        stage.close();
    }
}
