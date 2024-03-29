package org.panther;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Connection connection;

    public static Connection getConnection() {
        try {
            // Check if the connection is null or closed
            System.out.println("Attempting to connect to database");
            if (connection == null || connection.isClosed()) {
                String url = "jdbc:mysql://localhost:3305/FlyingFluffyPanthers"; // Your database URL and name
                String user = "fluffy"; // Your database username
                String password = "FFHFFFFFS"; // Your database password

                // Establish a new connection

                connection = DriverManager.getConnection(url, user, password);

                System.out.println("Connected to database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // or handle it differently
        }
        return connection;
    }
}

