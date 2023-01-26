import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class ExampleService {
    private final long chatId;
    private final PostgreSQLJDBS jdbs;
    private final SendMessage message;
    private final String botUsername;
    private final EnglishForEveryDayBot engBot;
    private final String botToken;

    ExampleService(String botUsername, String botToken, long chatId) {
        this.chatId = chatId;
        this.jdbs = PostgreSQLJDBS.getInstance();
        this.message = new SendMessage();
        this.botUsername = botUsername;
        this.botToken = botToken;
        engBot = new EnglishForEveryDayBot(this.botUsername, this.botToken);
    }

    @SneakyThrows
    public void getExample() {
        int day = jdbs.getUsersDay(chatId);
        String example = jdbs.getExample(day);
        message.setChatId(chatId);
        message.setText(example);
        engBot.execute(message);
    }
}