import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramCommandService {
    private final String botUsername;
    private final String botToken;
    private final EnglishForEveryDayBot engBot;
    private final SendMessage message;
    private final Results results;

    TelegramCommandService(String botUsername, String botToken, EnglishForEveryDayBot engBot) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.engBot = engBot;
        this.message = new SendMessage();
        results = new Results();
    }

    public void start(long chatId) {
        Registration reg = new Registration();
        if (!reg.checkUser(chatId)) {
            sendMessage(reg.text, chatId);
            reg.insertInDB(chatId);
        }
    }

    public void test(long chatId) {
        TestExecute testExecute = new TestExecute(botUsername, botToken, chatId);
        testExecute.start();
    }

    public void example(long chatId) {
        ExampleService exampleService = new ExampleService(botUsername, botToken, chatId);
        exampleService.getExample();
    }

    public void getLastTestRes(long chatId) {
        sendMessage(results.getLastResult(chatId), chatId);
    }

    public void getTotalRes(long chatId) {
        sendMessage(results.getTotalResult(chatId), chatId);
    }

    public void sendMessage(String messageText, long chatId) {
        message.setChatId(chatId);
        message.setText(messageText);

        try {
            engBot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
