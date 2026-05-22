package server.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Håndterer forbindelsen til PostgreSQL-databasen
public class DatabaseHelper {

    private static final String URL      = "jdbc:postgresql://localhost:5432/fleet_manager";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "postgres123";

    // Returnerer en ny forbindelse til databasen
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
