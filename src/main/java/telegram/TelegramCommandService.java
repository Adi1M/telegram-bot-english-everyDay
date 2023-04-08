package telegram;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.auth.AuthServiceImpl;
import service.auth.AuthUserService;
import service.database.DatabaseService;
import service.languagetest.TelegramLanguageTestService;
import service.languagetest.TelegramLanguageTestServiceImpl;

public class TelegramCommandService {
    private final AuthUserService authUserService;
    private final AbsSender sender;
    private final TelegramLanguageTestService languageTestService;

    public TelegramCommandService(AbsSender sender, DatabaseService databaseService) {
        this.sender = sender;
        this.authUserService = new AuthServiceImpl(databaseService);
        this.languageTestService = TelegramLanguageTestServiceImpl.getInstance(databaseService, sender);
    }

    public void start(long chatId) {
        if (!authUserService.checkUser(chatId)) {
            sendMessage(authUserService.getWelcomeText(), chatId);
            authUserService.registerUser(chatId);
        }
    }

    public void test(long chatId) throws TelegramApiException {
        languageTestService.startUserTest(chatId);
    }

    public void example(long chatId) throws TelegramApiException {
        languageTestService.getExample(chatId);
    }

    public void getLastTestRes(long chatId) {
        sendMessage(languageTestService.getLastResult(chatId), chatId);
    }

    public void getTotalRes(long chatId) {
        sendMessage(languageTestService.getTotalResult(chatId), chatId);
    }

    public void sendMessage(String messageText, long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(messageText);

        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
