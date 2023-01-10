package bot;

import connnection.PostgreSQLJDBS;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.*;

@Slf4j
public class EnglishForEveryDayBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    static final PostgreSQLJDBS postgreSQLJDBS = new PostgreSQLJDBS();
    public EnglishForEveryDayBot(String botUsername, String botToken){
        this.botToken = botToken;
        this.botUsername = botUsername;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        String messageFromUser = update.getMessage().getText();
        switch (messageFromUser) {
            case "/start" -> {
                long chatId = update.getMessage().getChatId();
                String username = update.getMessage().getChat().getFirstName();
                String text = "Welcome " + username +  " , to our Educational bot. Here you can  learn english with passive actions. \n" +
                        "\n" +
                        "What our bot can do ?\n" +
                        "Everyday you will get one English word\n" +
                        "You can watch example with this word\n" +
                        "Every week after starting education you can pass mini-quiz with words that you learned in these 7 days.\n" +
                        "\n" +
                        "Commands:\n" +
                        "/todaysWord\n" +
                        "/example With Today s Word\n" +
                        "/last Quiz\n" +
                        "/results\n" +
                        "//stop Learning\n" +
                        "/continueLearning";
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(text);

                try {
                    execute(message); // Sending our message object to user
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                postgreSQLJDBS.insertUser(chatId);

            }
        }


    }
}
