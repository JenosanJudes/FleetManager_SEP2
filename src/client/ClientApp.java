package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Start-punkt for klienten
public class ClientApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        AppContext.setPrimaryStage(primaryStage);

        // Opret forbindelsen til serveren
        AppContext.connect();

        // Åbn login-skærmen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/view/login.fxml"));
        Scene scene = new Scene(loader.load(), 450, 350);
        primaryStage.setTitle("Fleet Manager — Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
