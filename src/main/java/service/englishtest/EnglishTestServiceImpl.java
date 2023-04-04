package service.englishtest;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.database.DatabaseService;

public class EnglishTestServiceImpl implements EnglishTestService {
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
        String[] words = databaseService.getWord(7 * (week - 1) + numOfAns);
        if (answer.trim().equals(words[0].trim())) {
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
        }else {
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
}
