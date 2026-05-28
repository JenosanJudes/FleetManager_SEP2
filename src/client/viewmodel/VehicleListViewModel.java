package client.viewmodel;

import client.AppContext;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import shared.domain.Vehicle;
import shared.protocol.Request;
import shared.protocol.RequestType;
import shared.protocol.Response;

import java.util.List;

// ViewModel for bils-listen - henter og holder styr på biler
public class VehicleListViewModel {

    private final ObservableList<Vehicle> vehicles = FXCollections.observableArrayList();
    private final StringProperty searchText = new SimpleStringProperty("");
    private final StringProperty statusMessage = new SimpleStringProperty("");

    public VehicleListViewModel() {
        // Lyt på server-push: genindlæs biler hvis en anden klient tilføjer/ændrer/tildeler
        AppContext.getConnection().addPushListener(event -> {
            if ("VEHICLES_UPDATED".equals(event)) {
                loadVehicles();
            }
        });
    }

    // Henter alle biler fra serveren i en baggrundstråd
    public void loadVehicles() {
        new Thread(() -> {
            try {
                Request request = new Request(RequestType.GET_ALL_VEHICLES, null);
                Response response = AppContext.getConnection().send(request);

                if (response.isSuccess()) {
                    List<Vehicle> list = (List<Vehicle>) response.getData();
                    // Opdater UI på JavaFX-tråden
                    Platform.runLater(() -> vehicles.setAll(list));
                } else {
                    Platform.runLater(() -> statusMessage.set(response.getMessage()));
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusMessage.set("Fejl: " + e.getMessage()));
            }
        }).start();
    }

    // Søger på reg.nr. i baggrunden
    public void search() {
        new Thread(() -> {
            try {
                Request request = new Request(RequestType.SEARCH_VEHICLES, searchText.get());
                Response response = AppContext.getConnection().send(request);

                if (response.isSuccess()) {
                    List<Vehicle> list = (List<Vehicle>) response.getData();
                    Platform.runLater(() -> vehicles.setAll(list));
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusMessage.set("Fejl: " + e.getMessage()));
            }
        }).start();
    }

    // Gemmer en ny eller opdateret bil
    public void saveVehicle(Vehicle vehicle) {
        new Thread(() -> {
            try {
                boolean isNew = vehicle.id() == 0;
                RequestType type = isNew ? RequestType.CREATE_VEHICLE : RequestType.UPDATE_VEHICLE;
                Request request = new Request(type, vehicle);
                Response response = AppContext.getConnection().send(request);

                if (response.isSuccess()) {
                    loadVehicles(); // Genindlæs listen
                } else {
                    Platform.runLater(() -> statusMessage.set(response.getMessage()));
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusMessage.set("Fejl: " + e.getMessage()));
            }
        }).start();
    }

    public ObservableList<Vehicle> getVehicles()    { return vehicles; }
    public StringProperty searchTextProperty()      { return searchText; }
    public StringProperty statusMessageProperty()   { return statusMessage; }
}
