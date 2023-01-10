import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.Properties;

public class RegistrationService {

    private final String text;

    @SneakyThrows
    RegistrationService() {
        InputStream inputStream = RegistrationService.class.getClassLoader().getResourceAsStream("bot_resources.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        text = properties.getProperty("welcomeText");
        System.out.println(text);
    }

    public void insertInDB(long chatId) {
        PostgreSQLJDBS postgreSQLJDBS = new PostgreSQLJDBS();
        postgreSQLJDBS.insertUser(chatId);
    }

    public String getText() {
        return text;
    }

    public boolean checkUser(long chatId) {
        PostgreSQLJDBS postgreSQLJDBS = new PostgreSQLJDBS();
        postgreSQLJDBS.insertUser(chatId);
        return postgreSQLJDBS.checkUser(chatId);
    }
}
