package ir.maktabsharif.smspanel.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Single place where the JDBC connection settings live.
 */
public final class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/sms_panel";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
