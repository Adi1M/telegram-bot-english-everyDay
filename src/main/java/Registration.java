import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.Properties;

public class Registration {
    String text;

    @SneakyThrows
    Registration() {
        InputStream inputStream = Registration.class.getClassLoader().getResourceAsStream("bot_resources.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        this.text = properties.getProperty("welcomeText");
    }

    public void insertInDB(long chatId) {
        PostgreSQLJDBS.getInstance().insertUser(chatId);
    }

    public boolean checkUser(long chatId) {
        return PostgreSQLJDBS.getInstance().checkUser(chatId);
    }
}
