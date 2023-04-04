import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import service.database.DatabaseService;

public class ExampleService {
    private final AbsSender sender;
    private final long chatId;
    private final DatabaseService databaseService;
    private final SendMessage message;

    public ExampleService(AbsSender sender, long chatId, DatabaseService databaseService) {
        this.chatId = chatId;
        this.databaseService = databaseService;
        this.message = new SendMessage();
        this.sender = sender;
    }

    @SneakyThrows
    public void getExample() {
        int day = databaseService.getUserDay(chatId);
        String example = databaseService.getExample(day);
        message.setChatId(chatId);
        message.setText(example);
        sender.execute(message);
    }
}