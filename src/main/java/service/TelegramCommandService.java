package service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.auth.AuthUserService;
import service.auth.AuthServiceImpl;
import service.database.DatabaseService;
import service.englishtest.EnglishTestService;
import service.englishtest.EnglishTestServiceImpl;

public class TelegramCommandService {
    private final DatabaseService databaseService;
    private final AuthUserService authUserService;
    private final AbsSender sender;
    private final SendMessage message;

    private final EnglishTestService englishTestService;

    public TelegramCommandService(AbsSender sender, DatabaseService databaseService) {
        this.sender = sender;
        this.databaseService = databaseService;
        this.message = new SendMessage();
        this.authUserService = new AuthServiceImpl(databaseService);
        this.englishTestService = new EnglishTestServiceImpl(databaseService, sender);
    }

    public void start(long chatId) {
        if (!authUserService.checkUser(chatId)) {
            sendMessage(authUserService.getWelcomeText(), chatId);
            authUserService.registerUser(chatId);
        }
    }

    public void test(long chatId) {
        TestExecuteService testExecute = new TestExecuteService(sender, databaseService, chatId);
        testExecute.start();
    }

    public void example(long chatId) {
        englishTestService.getExample(chatId);
    }

    public void getLastTestRes(long chatId) {
        sendMessage(englishTestService.getLastResult(chatId), chatId);
    }

    public void getTotalRes(long chatId) {
        sendMessage(englishTestService.getTotalResult(chatId), chatId);
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
