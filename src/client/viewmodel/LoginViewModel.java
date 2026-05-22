package client.viewmodel;

import client.AppContext;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import shared.domain.User;
import shared.protocol.Request;
import shared.protocol.RequestType;
import shared.protocol.Response;

// ViewModel for login-skærmen - holder styr på hvad brugeren har tastet
public class LoginViewModel {

    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");

    // Forsøger at logge ind - returnerer true hvis det lykkedes
    public boolean login() {
        errorMessage.set("");

        if (username.get().isBlank() || password.get().isBlank()) {
            errorMessage.set("Udfyld brugernavn og password");
            return false;
        }

        try {
            String[] credentials = { username.get(), password.get() };
            Request request = new Request(RequestType.LOGIN, credentials);
            Response response = AppContext.getConnection().send(request);

            if (response.isSuccess()) {
                AppContext.setCurrentUser((User) response.getData());
                return true;
            } else {
                errorMessage.set(response.getMessage());
                return false;
            }
        } catch (Exception e) {
            errorMessage.set("Kunne ikke forbinde til serveren");
            return false;
        }
    }

    public StringProperty usernameProperty()     { return username; }
    public StringProperty passwordProperty()     { return password; }
    public StringProperty errorMessageProperty() { return errorMessage; }
}
