package connnection;

import lombok.SneakyThrows;

import java.sql.*;

public class PostgreSQLJDBS {
    private Statement stmt = null;

    public PostgreSQLJDBS() {
        try {
            Class.forName("org.postgresql.Driver");
            String DB_URL = "jdbc:postgresql://localhost:5432/engBot";
            String USER = "postgres";
            String PASS = "postgrespass";
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Adil + Syr");
    }
    public static void main(String[] args) {

    }


    @SneakyThrows
    public void insertUser(long chatId){
        String userExists = "SELECT chatid from users where chatid = " + chatId;
        ResultSet rs = stmt.executeQuery(userExists);
        boolean t = rs.next();
        if(!t) {
            System.out.println("Inserting records into the table...");
            String sql = "INSERT INTO users VALUES (" + chatId + ", 1, true)";
            stmt.executeUpdate(sql);
        }
    }
}
