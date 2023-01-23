import lombok.SneakyThrows;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
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

    @SneakyThrows
    public ArrayList<Long> getChatIds() {
        ArrayList<Long> res = new ArrayList<>();
        String getChatId = "SELECT chatid from users";
        ResultSet rs = stmt.executeQuery(getChatId);
        while (rs.next()) {
            res.add((long)rs.getInt("chatid"));
        }
        return res;
    }

    @SneakyThrows
    public int getUsersDay(long chatId) {
        String userExists = String.format("SELECT day from users where chatid = %d",chatId);
        ResultSet rs = stmt.executeQuery(userExists);
        if(rs.next()) {
            return rs.getInt("day");
        }
        return 1;
    }

    @SneakyThrows
    public void updateUsersDay(long chatId, int day) {
        String update = String.format("Update users set day = %s where chatid = %d",day,chatId);
        stmt.executeUpdate(update);
    }

    @SneakyThrows
    public String[] getWord(int day){
        String EngWordReq = "Select english,translation from words where id = " + day;
        ResultSet rs = stmt.executeQuery(EngWordReq);
        if(rs.next())
        return new String[]{rs.getString("english"), rs.getString("translation")};
        else return null;
    }

    @SneakyThrows
    public void updateResults(long chatId, int week) {
        String select = String.format("Select result from results where chatid = %d and week = %d",chatId,week);
        ResultSet rs = stmt.executeQuery(select);
        int result = 0;
        if(rs.next()) {
            result = rs.getInt("result");
        }
        result+=1;
        System.out.println("result " + result);
            String update = String.format("Update results set result = %d where chatid = %d and week = %d", result, chatId, week);
            stmt.executeUpdate(update);
    }

    @SneakyThrows
    public void insertToResults(long chatId, int week) {
        String sql = String.format("INSERT INTO results VALUES (%d, 0, %d)",chatId,week);
        stmt.executeUpdate(sql);
    }

    @SneakyThrows
    public boolean hasTested(long chatId, int week) {
        String select = String.format("Select * from results where chatid = %d and week = %d",chatId,week);
        ResultSet rs = stmt.executeQuery(select);
        return rs.next();
    }


}
