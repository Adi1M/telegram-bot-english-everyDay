import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;

public class NotifierService extends Thread{

    private final String botUsername;
    private final String botToken;

    NotifierService(String botUsername, String botToken) {
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            LocalDateTime now = LocalDateTime.now();

            int nTimeHour = 11;
            int nTimeMinute = 00;

            long needms = nTimeHour*60*60*1000 + nTimeMinute*60*1000;
            long curr = now.getLong(ChronoField.MILLI_OF_DAY);

            long diff = Math.abs(needms - curr);

            long timeToSLeep = 0;

            if (curr > needms) {
                timeToSLeep = 24*60*60*1000 - diff;
            } else {
                timeToSLeep = diff;

            }

            Thread.sleep(timeToSLeep);
            PostgreSQLJDBS postgreSQLJDBS = new PostgreSQLJDBS();
            ArrayList<Long> chatIds = postgreSQLJDBS.getChatIds();
            EnglishForEveryDayBot engBot = new EnglishForEveryDayBot(botUsername,botToken);
            for(long chatId: chatIds) {
                int day = postgreSQLJDBS.getUsersDay(chatId);
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                String[] words = postgreSQLJDBS.getWord(day);
                message.setText(words[0] + " - " + words[1]);
                engBot.execute(message);
                postgreSQLJDBS.updateUsersDay(chatId,day+1);

            }
        }
    }
}
