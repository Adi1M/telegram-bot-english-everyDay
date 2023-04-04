import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.database.DatabaseService;

public class TelegramCommandService {
    private final DatabaseService databaseService;
    private final AbsSender sender;
    private final SendMessage message;
    private final Results results;

    TelegramCommandService(AbsSender sender, DatabaseService databaseService) {
        this.sender = sender;
        this.databaseService = databaseService;
        this.message = new SendMessage();
        results = new Results(databaseService);
    }

    public void start(long chatId) {
        Registration reg = new Registration(databaseService);
        if (!reg.checkUser(chatId)) {
            sendMessage(reg.text, chatId);
            reg.insertInDB(chatId);
        }
    }

    public void test(long chatId) {
        TestExecute testExecute = new TestExecute(sender, databaseService, chatId);
        testExecute.start();
    }

    public void example(long chatId) {
        ExampleService exampleService = new ExampleService(sender, chatId, databaseService);
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
            sender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
