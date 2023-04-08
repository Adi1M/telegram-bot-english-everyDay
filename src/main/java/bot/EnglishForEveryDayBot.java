package bot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.database.DatabaseService;
import telegram.TelegramCommandService;
import service.languagetest.TelegramLanguageTestServiceImpl;

@Slf4j
public class EnglishForEveryDayBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    private final TelegramLanguageTestServiceImpl englishTestService;
    private final TelegramCommandService commands;
    SendMessage message;

    public EnglishForEveryDayBot(String botUsername, String botToken,
                                 DatabaseService databaseService) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.message = new SendMessage();
        this.englishTestService = TelegramLanguageTestServiceImpl.getInstance(databaseService, this);
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

    @SneakyThrows(TelegramApiException.class)
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
