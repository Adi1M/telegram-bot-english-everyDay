
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.net.http.HttpClient;

@Slf4j
public class EnglishForEveryDayBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;

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

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageFromUser = update.getMessage().getText();

            switch (messageFromUser) {
                case "/start" -> {
                    RegistrationService reg = new RegistrationService();
                    long chatId = update.getMessage().getChatId();

                    if(!reg.checkUser(chatId)) {
                        SendMessage message = new SendMessage();
                        message.setChatId(chatId);
                        message.setText(reg.getText());

                        try {
                            execute(message); // Sending our message object to user
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }

                        reg.insertInDB(chatId);
                    }
                }
            }
        }

    }
}
