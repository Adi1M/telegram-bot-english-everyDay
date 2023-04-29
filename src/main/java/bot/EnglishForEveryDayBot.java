package bot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import service.englishtest.EnglishTestService;
import service.englishtest.EnglishTestServiceImpl;
import service.TelegramCommandService;
import service.database.DatabaseService;

@Slf4j
public class EnglishForEveryDayBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    private final EnglishTestService englishTestService;
    private final TelegramCommandService commands;
    SendMessage message;

    public EnglishForEveryDayBot(String botUsername, String botToken,
                                 DatabaseService databaseService) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.message = new SendMessage();
        this.englishTestService = new EnglishTestServiceImpl(databaseService, this);
        this.commands = new TelegramCommandService(this, databaseService);
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
            englishTestService.getResultOfAnswer(chatId, update.getCallbackQuery().getData());
        }
    }
}
