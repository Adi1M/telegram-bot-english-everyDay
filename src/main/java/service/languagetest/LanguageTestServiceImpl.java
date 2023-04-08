package service.languagetest;

import com.vdurmont.emoji.EmojiParser;
import enums.LangTemplate;
import lombok.extern.slf4j.Slf4j;
import service.database.DatabaseService;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class LanguageTestServiceImpl implements LanguageTestService {
    private static LanguageTestServiceImpl englishTestService;
    private final DatabaseService databaseService;


    private LanguageTestServiceImpl(DatabaseService databaseService) {
        this.databaseService = databaseService;
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
    public String getResultOfAnswer(long userId, String answer) {
        int indOfAns = answer.charAt(0) - '0';
        if (receiveAnswer(userId, answer.substring(2), indOfAns)) {
            return EmojiParser.parseToUnicode(":white_check_mark:");
        }
        return EmojiParser.parseToUnicode(":x:");
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
    public String getExample(long userId) {
        return databaseService.getExampleByDay(userId);
    }

    public static LanguageTestServiceImpl getInstance(DatabaseService databaseService) {
        return Objects.requireNonNullElseGet(englishTestService,
                () -> englishTestService = new LanguageTestServiceImpl(databaseService));
    }

    @Override
    public Map<LangTemplate, String> getWord(int day) {
        return databaseService.getWord(day);
    }

    @Override
    public List<Map<LangTemplate, String>> getWordWithRange(Long userId, int range) {
        return databaseService.getWordWithRange(userId, range);
    }
}
