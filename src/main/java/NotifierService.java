import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;

public class NotifierService extends Thread {
    private static NotifierService notifierService;
    private final PostgreSQLJDBS postgreSQLJDBS;
    private final String botUsername;
    private final String botToken;
    private final SendMessage message;

    private NotifierService(String botUsername, String botToken) {
        this.message = new SendMessage();
        this.postgreSQLJDBS = PostgreSQLJDBS.getInstance();
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    public static NotifierService getInstance(String botToken, String botUsername) {
        if (notifierService == null) return new NotifierService(botToken, botUsername);
        else return notifierService;
    }

    @SneakyThrows
    @Override
    public void run() {

        while (true) {
            LocalDateTime now = LocalDateTime.now();

            int timeHour = 11;
            int timeMinute = 00;

            long needms = timeHour * 60 * 60 * 1000 + timeMinute * 60 * 1000;
            long curr = now.getLong(ChronoField.MILLI_OF_DAY);

            long diff = Math.abs(needms - curr);

            long timeToSLeep = 0;

            if (curr > needms) {
                timeToSLeep = 24 * 60 * 60 * 1000 - diff;
            } else {
                timeToSLeep = diff;

            }
            Thread.sleep(timeToSLeep);

            ArrayList<Long> chatIds = this.postgreSQLJDBS.getChatIds();
            EnglishForEveryDayBot engBot = new EnglishForEveryDayBot(this.botUsername, this.botToken);
            for (long chatId : chatIds) {
                int day = this.postgreSQLJDBS.getUsersDay(chatId);
                this.message.setChatId(chatId);
                String[] words = this.postgreSQLJDBS.getWord(day);
                this.message.setText(words[0] + " - " + words[1]);
                engBot.execute(this.message);

                this.postgreSQLJDBS.updateUsersDay(chatId, day + 1);
            }
        }
    }
}
