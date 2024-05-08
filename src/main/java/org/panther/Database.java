package org.panther;

import org.panther.Utilities.AppLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Database {
    private static final Logger LOGGER = AppLogger.getLogger();

    private static Connection connection;

    public static Connection getConnection() {
        try {
            // Check if the connection is null or closed
            //System.out.println("Attempting to connect to database");
            if (connection == null || connection.isClosed()) {
                //String url = "jdbc:mysql://localhost:3306/flyingfluffypanthers"; // host computer
                String url = "jdbc:mysql://localhost:3305/flyingfluffypanthers"; //  testing desktop server
                String user = "fluffy"; // Your database username
                String password = "FFHFFFFFS"; // Your database password

                // Establish a new connection

                connection = DriverManager.getConnection(url, user, password);

                //System.out.println("Connected to database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // or handle it differently
        }
        return connection;
    }
}

