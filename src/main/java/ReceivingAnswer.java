import com.vdurmont.emoji.EmojiParser;

public class ReceivingAnswer {
    PostgreSQLJDBS postgreSQLJDBS;
    private final long chatId;
    TelegramCommandService commands;

    ReceivingAnswer(long chatId, String botUsername, String botToken) {
        postgreSQLJDBS = PostgreSQLJDBS.getInstance();
        this.chatId = chatId;
        this.commands = new TelegramCommandService(botUsername, botToken, new EnglishForEveryDayBot(botUsername, botToken));
    }

    public boolean receiveAnswer(String ans, int numOfAns) {
        int day = this.postgreSQLJDBS.getUsersDay(this.chatId);
        int week = day / 7;
        String[] words = this.postgreSQLJDBS.getWord(7 * (week - 1) + numOfAns);
        if (ans.trim().equals(words[0].trim())) {
            this.postgreSQLJDBS.updateResults(this.chatId, week);
            return true;
        }
        return false;
    }

    public void getResultOfAnswer(String answer) {
        int indOfAns = answer.charAt(0) - '0';
        String checkEmoji = "";
        if (receiveAnswer(answer.substring(2), indOfAns)) {
            checkEmoji = EmojiParser.parseToUnicode(":white_check_mark:");
        } else checkEmoji = EmojiParser.parseToUnicode(":x:");

        commands.sendMessage(checkEmoji, chatId);
    }
}
