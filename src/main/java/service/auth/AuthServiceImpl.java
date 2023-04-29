package service.auth;

import lombok.SneakyThrows;
import service.database.DatabaseService;

import java.io.InputStream;
import java.util.Properties;

public class AuthServiceImpl implements AuthUserService {
    private final DatabaseService databaseService;
    private final String text;

    @SneakyThrows
    public AuthServiceImpl(DatabaseService databaseService) {
        this.databaseService = databaseService;
        //FIXME need to refactor.
        // insert properties to constructor and add this property from Main.java
        Properties properties = getProperties();
        this.text = properties.getProperty("welcomeText");
    }

    @SneakyThrows
    //FIXME need to delete this
    public Properties getProperties() {
        InputStream inputStream = AuthServiceImpl.class.getClassLoader().getResourceAsStream("bot_resources.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        return properties;
    }

    @Override
    public void registerUser(long chatId) {
        databaseService.createUser(chatId);
    }

    @Override
    public boolean checkUser(long chatId) {
        return databaseService.checkUser(chatId);
    }

    @Override
    public String getWelcomeText() {
        return text;
    }
}
