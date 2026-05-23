package client.viewmodel;

import client.AppContext;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.domain.Employee;
import shared.protocol.Request;
import shared.protocol.RequestType;
import shared.protocol.Response;

import java.util.List;

// ViewModel for medarbejder-listen
public class EmployeeListViewModel {

    private final ObservableList<Employee> employees = FXCollections.observableArrayList();
    private final StringProperty searchText    = new SimpleStringProperty("");
    private final StringProperty statusMessage = new SimpleStringProperty("");

    // Henter alle medarbejdere fra serveren i baggrunden
    public void loadEmployees() {
        new Thread(() -> {
            try {
                Request request = new Request(RequestType.GET_ALL_EMPLOYEES, null);
                Response response = AppContext.getConnection().send(request);

                if (response.isSuccess()) {
                    List<Employee> list = (List<Employee>) response.getData();
                    Platform.runLater(() -> employees.setAll(list));
                } else {
                    Platform.runLater(() -> statusMessage.set(response.getMessage()));
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusMessage.set("Fejl: " + e.getMessage()));
            }
        }).start();
    }

    // Søger på navn
    public void search() {
        String query = searchText.get().toLowerCase();
        new Thread(() -> {
            try {
                Request request = new Request(RequestType.GET_ALL_EMPLOYEES, null);
                Response response = AppContext.getConnection().send(request);

                if (response.isSuccess()) {
                    List<Employee> all = (List<Employee>) response.getData();
                    List<Employee> filtered = all.stream()
                            .filter(e -> e.fullName().toLowerCase().contains(query))
                            .toList();
                    Platform.runLater(() -> employees.setAll(filtered));
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusMessage.set("Fejl: " + e.getMessage()));
            }
        }).start();
    }

    // Gemmer en ny eller opdateret medarbejder
    public void saveEmployee(Employee employee) {
        new Thread(() -> {
            try {
                boolean isNew = employee.id() == 0;
                RequestType type = isNew ? RequestType.CREATE_EMPLOYEE : RequestType.UPDATE_EMPLOYEE;
                Request request = new Request(type, employee);
                Response response = AppContext.getConnection().send(request);

                if (response.isSuccess()) {
                    loadEmployees();
                } else {
                    Platform.runLater(() -> statusMessage.set(response.getMessage()));
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusMessage.set("Fejl: " + e.getMessage()));
            }
        }).start();
    }

    public ObservableList<Employee> getEmployees() { return employees; }
    public StringProperty searchTextProperty()     { return searchText; }
    public StringProperty statusMessageProperty()  { return statusMessage; }
}
