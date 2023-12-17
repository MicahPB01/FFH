package org.DiscordBot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Connection connection;

    public static Connection getConnection() {
        try {
            // Check if the connection is null or closed
            if (connection == null || connection.isClosed()) {
                String url = "jdbc:mysql://localhost:3306/discordbot"; // Your database URL and name
                String user = "root"; // Your database username
                String password = "alpine"; // Your database password

                // Establish a new connection
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // or handle it differently
        }
        return connection;
    }
}
