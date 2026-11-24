package main.com.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class DatabaseConnection {

    private static final Properties properties = new Properties();

    static {
        try (FileInputStream file = new FileInputStream(".env")) {
            properties.load(file);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                    "Unable to load .env file: " + e.getMessage());
        }
    }

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {

        String url = properties.getProperty("DB_URL");
        String user = properties.getProperty("DB_USER");
        String password = properties.getProperty("DB_PASSWORD");

        if (url == null || user == null || password == null) {
            throw new IllegalStateException(
                    "Database configuration is incomplete.");
        }

        return DriverManager.getConnection(url, user, password);
    }
}