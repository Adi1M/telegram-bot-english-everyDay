
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


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
                        Registration reg = new Registration();
                        long chatId = update.getMessage().getChatId();

                        if (!reg.checkUser(chatId)) {
                            SendMessage message = new SendMessage();
                            message.setChatId(chatId);
                            message.setText(reg.text);

                            try {
                                execute(message); 
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }

                            reg.insertInDB(chatId);

                        }
                    }
                    case "/test" -> {
                        long chatId = update.getMessage().getChatId();
                        TestExecute testExecute = new TestExecute(this.botUsername,this.botToken,chatId);
                        testExecute.start();
                    }
                    case "/lastTest" -> {
                        long chatId = update.getMessage().getChatId();
                        Results results = new Results(chatId);

                        SendMessage message = new SendMessage();
                        message.setChatId(chatId);
                        message.setText(results.getLastResult());

                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    case "/totalScore" -> {
                        long chatId = update.getMessage().getChatId();
                        Results results = new Results(chatId);

                        SendMessage message = new SendMessage();
                        message.setChatId(chatId);
                        message.setText(results.getTotalResult());

                        try {
                            execute(message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else {
                long chatId = update.getMessage().getChatId();
                int indOfAns = messageFromUser.charAt(0) - '0';
                ReceivingAnswer receivingAnsFromPoll = new ReceivingAnswer(chatId);
                receivingAnsFromPoll.receiveAnswer(messageFromUser.substring(5),indOfAns);
            }
        }
    }
}
