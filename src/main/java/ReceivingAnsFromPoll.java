import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.api.objects.polls.PollOption;

import java.util.List;

public class ReceivingAnsFromPoll {

    private final String botUsername;
    private final String botToken;
    private long chatId;

    ReceivingAnsFromPoll(String botUsername, String botToken,long chatId) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.chatId = chatId;
    }

    public void receiveAns(String ans,int numOfAns) {
            PostgreSQLJDBS postgreSQLJDBS = new PostgreSQLJDBS();
            int day = postgreSQLJDBS.getUsersDay(chatId);
            int week = day/7;
            String[] words = postgreSQLJDBS.getWord(7*(week-1) + numOfAns);
            System.out.println(ans + " " + words[0]);
            System.out.println("week " + week );
            System.out.println(ans.trim().equals(words[0].trim()));
            if(ans.trim().equals(words[0].trim())) {
                postgreSQLJDBS.updateResults(chatId, week);
            }
    }
}
