package service.englishtest;

import com.vdurmont.emoji.EmojiParser;
import enums.LangTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pojo.EnglishUserTest;
import service.database.DatabaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EnglishTestServiceImpl implements EnglishTestService {
    private final static int DEFAULT_TEST_RANGE_DAYS = 7;
    private final DatabaseService databaseService;
    private final AbsSender sender;

    public EnglishTestServiceImpl(DatabaseService databaseService,
                                  AbsSender sender) {
        this.databaseService = databaseService;
        this.sender = sender;
    }

    @Override
    public boolean receiveAnswer(long userId, String answer, int numOfAns) {
        int day = databaseService.getUserDay(userId);
        int week = day / 7;
        Map<LangTemplate, String> words = databaseService.getWord(7 * (week - 1) + numOfAns);
        if (answer.trim().equals(words.get(LangTemplate.TARGET).trim())) {
            databaseService.updateResults(userId, week);
            return true;
        }
        return false;
    }

    @Override
    public void getResultOfAnswer(long userId, String answer) {
        int indOfAns = answer.charAt(0) - '0';
        String checkEmoji;
        if (receiveAnswer(userId, answer.substring(2), indOfAns)) {
            checkEmoji = EmojiParser.parseToUnicode(":white_check_mark:");
        } else checkEmoji = EmojiParser.parseToUnicode(":x:");

        try {
            sender.execute(new SendMessage(String.valueOf(userId), checkEmoji));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getLastResult(long userId) {
        int day = databaseService.getUserDay(userId);
        int week = day / 7;
        int userResult = databaseService.getLastTestResult(userId, week - 1);

        if (userResult == -1) {
            return "You didn't take the test last week";
        } else {
            return "Your last test result " + userResult + "/7";
        }
    }

    @Override
    public String getTotalResult(long userId) {
        return databaseService.getTotalResult(userId);
    }

    @Override
    public void getExample(long userId) {
        int day = databaseService.getUserDay(userId);
        String example = databaseService.getExample(day);
        try {
            sender.execute(new SendMessage(String.valueOf(userId), example));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void initEnglishTest(Long userId) {
        List<String> answers = new ArrayList<>();
        int day = databaseService.getUserDay(userId);
        List<Map<LangTemplate, String>> words = getWordsFromDatabaseByDay(day);
        // other words
        Random randomDay = new Random();
        for (int i = 0; i < 2; i++) {
            int rDay = randomDay.nextInt(day);
            databaseService.getWord(rDay);
        }

        EnglishUserTest userTest = new EnglishUserTest(userId, words);
    }

    private List<Map<LangTemplate, String>> getWordsFromDatabaseByDay(int day) {
        if (day < 7) {
            return databaseService.getWordWithRange(0, day);
        }
        return databaseService.getWordWithRange(day, DEFAULT_TEST_RANGE_DAYS);
    }
}
