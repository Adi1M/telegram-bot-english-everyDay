import connector.PostgresConnector;
import exception.DatabaseException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import service.database.DatabaseService;
import service.database.DatabaseServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException, TelegramApiException, DatabaseException {
        Properties botProperties = getPropertiesByName("telegram_bot_conf.properties");
        Properties databaseProperties = getPropertiesByName("database.properties");

        String botUsername = botProperties.getProperty("bot.username");
        String botToken = botProperties.getProperty("bot.token");

        DatabaseService databaseService = new DatabaseServiceImpl(
                PostgresConnector.getInstance(databaseProperties));

        EnglishForEveryDayBot bot = new EnglishForEveryDayBot(botUsername, botToken, databaseService);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);

        NotifierService.getInstance(bot, databaseService).start();
    }

    private static Properties getPropertiesByName(String name) throws IOException {
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(name);
        Properties properties = new Properties();
        properties.load(inputStream);

        return properties;
    }
}
