import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import service.database.DatabaseService;

@Slf4j
public class EnglishForEveryDayBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    private final DatabaseService databaseService;
    SendMessage message;

    public EnglishForEveryDayBot(String botUsername, String botToken,
                                 DatabaseService databaseService) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.databaseService = databaseService;
        this.message = new SendMessage();
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
        TelegramCommandService commands = new TelegramCommandService(
                this, databaseService
        );
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageFromUser = update.getMessage().getText();
            if (!messageFromUser.contains("->")) {
                long chatId = update.getMessage().getChatId();
                switch (messageFromUser) {
                    case "/start" -> commands.start(chatId);
                    case "/test" -> commands.test(chatId);
                    case "/example" -> commands.example(chatId);
                    case "/lastTest" -> commands.getLastTestRes(chatId);
                    case "/totalResult" -> commands.getTotalRes(chatId);
                    default -> commands.sendMessage("Wrong command", chatId);
                }
            }
        } else if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            ReceivingAnswer receivingAnsFromPoll = new ReceivingAnswer(chatId, this, databaseService);
            receivingAnsFromPoll.getResultOfAnswer(update.getCallbackQuery().getData());
        }
    }
}
