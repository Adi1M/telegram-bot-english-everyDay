import lombok.SneakyThrows;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class PostgreSQLJDBS {
    private Statement stmt = null;

    public PostgreSQLJDBS() {
        try {
            InputStream inputStream = RegistrationService.class.getClassLoader().getResourceAsStream("database.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            String DB_URL = properties.getProperty("url");
            String USER = properties.getProperty("user");
            String PASS = properties.getProperty("password");
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    @SneakyThrows
    public void insertUser(long chatId){
            String sql = "INSERT INTO users VALUES (" + chatId + ", 1, true)";
            stmt.executeUpdate(sql);
    }

    @SneakyThrows
    public boolean checkUser(long chatId) {
        String userExists = String.format("SELECT chatid from users where chatid = %d",chatId);
        ResultSet rs = stmt.executeQuery(userExists);
        boolean t = rs.next();

        return t;
    }
}
