
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollOption;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EnglishForEveryDayBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;

    public EnglishForEveryDayBot(String botUsername, String botToken) {
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
            if(!messageFromUser.contains("->")) {
                switch (messageFromUser) {
                    case "/start" -> {
                        RegistrationService reg = new RegistrationService();
                        long chatId = update.getMessage().getChatId();

                        if (!reg.checkUser(chatId)) {
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
                    case "/test" -> {
                        long chatId = update.getMessage().getChatId();
                        TestExecute testExecute = new TestExecute(botUsername,botToken,chatId);
                        testExecute.start();
                    }
                }
            }else {
                long chatId = update.getMessage().getChatId();
                int indOfAns = messageFromUser.charAt(0) - '0';
                ReceivingAnsFromPoll receivingAnsFromPoll = new ReceivingAnsFromPoll(botUsername,botToken,chatId);
                receivingAnsFromPoll.receiveAns(messageFromUser.substring(5),indOfAns);
            }
        }
    }
}
