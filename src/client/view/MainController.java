package client.view;

import client.AppContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;

// Controller for hovedvinduet med tabs
public class MainController {

    @FXML private Label userLabel;

    @FXML
    public void initialize() {
        // Vis den indloggede brugers navn øverst
        if (AppContext.getCurrentUser() != null) {
            userLabel.setText(AppContext.getCurrentUser().fullName());
        }
    }

    // Log ud og gå tilbage til login
    @FXML
    public void onLogoutClick() {
        try {
            AppContext.setCurrentUser(null);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/view/login.fxml"));
            Scene scene = new Scene(loader.load(), 450, 350);
            AppContext.getPrimaryStage().setScene(scene);
            AppContext.getPrimaryStage().setTitle("Fleet Manager — Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
