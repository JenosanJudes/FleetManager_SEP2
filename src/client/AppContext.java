package client;

import client.network.ServerConnection;
import javafx.stage.Stage;
import shared.domain.User;

import java.io.IOException;

// Holder styr på den globale tilstand - forbindelsen og den indloggede bruger
public class AppContext {

    private static ServerConnection connection;
    private static User currentUser;
    private static Stage primaryStage;

    // Opretter forbindelsen til serveren
    public static void connect() throws IOException {
        connection = new ServerConnection();
        connection.connect();
    }

    public static ServerConnection getConnection() {
        return connection;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }
}
