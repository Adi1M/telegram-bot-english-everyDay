package utils.connector;

import service.database.DatabaseException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgresConnector {
    private static PostgresConnector postgresConnector;
    private Connection connection;

    private PostgresConnector(Properties properties) throws DatabaseException {
        String DB_URL = properties.getProperty("url");
        String USER = properties.getProperty("user");
        String PASS = properties.getProperty("password");
        try {
            this.connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            throw new DatabaseException();
        }
    }

    public static PostgresConnector getInstance(Properties properties) throws DatabaseException {
        if (postgresConnector == null)
          return new PostgresConnector(properties);
        else
          return postgresConnector;
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if (connection.isClosed()) {
                connection.close();
            }
            connection = null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        postgresConnector = null;
    }
}
