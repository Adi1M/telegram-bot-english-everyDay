import lombok.SneakyThrows;
import service.database.DatabaseService;

import java.io.InputStream;
import java.util.Properties;

public class Registration {
    DatabaseService databaseService;
    String text;

    @SneakyThrows
    Registration(DatabaseService databaseService) {
        this.databaseService = databaseService;
        Properties properties = getProperties();
        this.text = properties.getProperty("welcomeText");
    }

    @SneakyThrows
    public Properties getProperties() {
        InputStream inputStream = Registration.class.getClassLoader().getResourceAsStream("bot_resources.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }

    public void insertInDB(long chatId) {
        databaseService.createUser(chatId);
    }

    public boolean checkUser(long chatId) {
        return databaseService.checkUser(chatId);
    }
}
