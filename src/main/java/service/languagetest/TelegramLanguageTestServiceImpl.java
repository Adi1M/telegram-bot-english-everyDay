package service.languagetest;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pojo.LanguageTest;
import service.database.DatabaseService;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class TelegramLanguageTestServiceImpl implements TelegramLanguageTestService {
    public final static int DEFAULT_TEST_RANGE_DAYS = 7;
    public final static int DEFAULT_TEST_WORD_SIZE = 2;
    public final static int DEFAULT_TEST_QUESTION_SECONDS = 2;
    private static TelegramLanguageTestServiceImpl telegramLanguageTestService;
    private final LanguageTestService languageTestService;
    private final AbsSender sender;
    private final List<LanguageTest> userTestList = new LinkedList<>();
    private final LanguageTestExecutor testExecutor;

    private TelegramLanguageTestServiceImpl(DatabaseService databaseService,
                                           AbsSender sender) {
        this.languageTestService = LanguageTestServiceImpl.getInstance(databaseService);
        this.sender = sender;
        this.testExecutor = new LanguageTestExecutor(sender, userTestList);
    }

    public void getResultOfAnswer(Long userId, String answer) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(userId);
        message.setText(languageTestService.getResultOfAnswer(userId, answer));
        sender.execute(message);
    }

    public void getExample(Long userId) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(userId);
        message.setText(languageTestService.getExample(userId));

        sender.execute(message);
    }

    @Override
    public void removeUserTest(Long userId) throws UserTestNotFoundException {
        synchronized (userTestList) {
            if (!userTestList.removeIf(t -> t.getUserId().equals(userId)))
                throw new UserTestNotFoundException();
        }
    }

    @Override
    public void startUserTest(Long userId) throws TelegramApiException {
        // if test is exist
        synchronized (userTestList) {
            if (userTestList.stream().anyMatch(test -> test.getUserId().equals(userId))) {
                log.warn("Test is exist. User id '{}'", userId);
                sender.execute(new SendMessage(userId.toString(), "Now u completing test"));
                return;
            }
            userTestList.add(LanguageTest.generateTest(userId, languageTestService));
            testExecutor.start();
        }
    }

    @Override
    public String getLastResult(long userId) {
        return languageTestService.getLastResult(userId);
    }

    @Override
    public String getTotalResult(long userId) {
        return languageTestService.getTotalResult(userId);
    }

    public static TelegramLanguageTestServiceImpl getInstance(DatabaseService databaseService,
                                                              AbsSender sender) {
        return Objects.requireNonNullElseGet(telegramLanguageTestService,
                () -> telegramLanguageTestService = new TelegramLanguageTestServiceImpl(databaseService, sender));
    }
}
