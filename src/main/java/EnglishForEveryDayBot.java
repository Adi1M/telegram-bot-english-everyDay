import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class EnglishForEveryDayBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    SendMessage message;

    public EnglishForEveryDayBot(String botUsername, String botToken) {
        this.botToken = botToken;
        this.botUsername = botUsername;
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
        TelegramCommandService commands = new TelegramCommandService(botUsername, botToken, new EnglishForEveryDayBot(botUsername, botToken));
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
            ReceivingAnswer receivingAnsFromPoll = new ReceivingAnswer(update.getCallbackQuery().getMessage().getChatId(), botUsername, botToken);
            receivingAnsFromPoll.getResultOfAnswer(update.getCallbackQuery().getData());
        }
    }
}
