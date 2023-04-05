import bot.EnglishForEveryDayBot;
import connector.PostgresConnector;
import exception.DatabaseException;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import service.NotifierService;
import service.database.DatabaseService;
import service.database.DatabaseServiceImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException, TelegramApiException, DatabaseException {
        Properties applicationProperties = getApplicationProperties();

        String botUsername = applicationProperties.getProperty("bot.username");
        String botToken = applicationProperties.getProperty("bot.token");

        if (botToken.isBlank() || botUsername.isBlank()) {
            throw new NullPointerException("Telegram bot token or username is blank.");
        }

        DatabaseService databaseService = new DatabaseServiceImpl(
                PostgresConnector.getInstance(applicationProperties));

        EnglishForEveryDayBot bot = new EnglishForEveryDayBot(botUsername, botToken, databaseService);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);

        NotifierService.getInstance(bot, databaseService).start();
    }

    private static Properties getApplicationProperties() throws IOException {
        InputStream inputStream = Main.class.getClassLoader()
                .getResourceAsStream("application.properties");
        Properties properties = new Properties();
        properties.load(inputStream);

        return properties;
    }
}
