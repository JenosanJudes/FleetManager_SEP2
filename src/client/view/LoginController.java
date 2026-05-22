package client.view;

import client.AppContext;
import client.viewmodel.LoginViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

// Controller for login-skærmen
public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label         errorLabel;

    private final LoginViewModel viewModel = new LoginViewModel();

    @FXML
    public void initialize() {
        // Bind felterne til viewmodellen
        usernameField.textProperty().bindBidirectional(viewModel.usernameProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
        errorLabel.textProperty().bind(viewModel.errorMessageProperty());
    }

    @FXML
    public void onLoginClick() {
        boolean success = viewModel.login();
        if (success) {
            openMainWindow();
        }
    }

    // Åbn hovedvinduet efter login
    private void openMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/view/main.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 750);
            AppContext.getPrimaryStage().setScene(scene);
            AppContext.getPrimaryStage().setTitle("Fleet Manager");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
