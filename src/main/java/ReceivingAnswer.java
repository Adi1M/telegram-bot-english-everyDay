import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.meta.bots.AbsSender;
import service.database.DatabaseService;

public class ReceivingAnswer {
    private final DatabaseService databaseService;
    private final long chatId;
    TelegramCommandService commands;

    ReceivingAnswer(long chatId, AbsSender sender, DatabaseService databaseService) {
        this.databaseService = databaseService;
        this.chatId = chatId;
        this.commands = new TelegramCommandService(sender, databaseService);
    }

    public boolean receiveAnswer(String ans, int numOfAns) {
        int day = databaseService.getUserDay(this.chatId);
        int week = day / 7;
        String[] words = databaseService.getWord(7 * (week - 1) + numOfAns);
        if (ans.trim().equals(words[0].trim())) {
            databaseService.updateResults(this.chatId, week);
            return true;
        }
        return false;
    }

    public void getResultOfAnswer(String answer) {
        int indOfAns = answer.charAt(0) - '0';
        String checkEmoji;
        if (receiveAnswer(answer.substring(2), indOfAns)) {
            checkEmoji = EmojiParser.parseToUnicode(":white_check_mark:");
        } else checkEmoji = EmojiParser.parseToUnicode(":x:");

        commands.sendMessage(checkEmoji, chatId);
    }
}
