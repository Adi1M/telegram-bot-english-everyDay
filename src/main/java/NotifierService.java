import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import service.database.DatabaseService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.List;

public class NotifierService extends Thread {
    private static NotifierService notifierService;
    private final DatabaseService databaseService;
    private final AbsSender sender;
    private final SendMessage message;

    private NotifierService(AbsSender sender, DatabaseService databaseService) {
        this.message = new SendMessage();
        this.databaseService = databaseService;
        this.sender = sender;
    }

    public static NotifierService getInstance(AbsSender sender,
                                              DatabaseService databaseService) {
        if (notifierService == null)
            return notifierService = new NotifierService(sender, databaseService);
        else
            return notifierService;
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

            List<Long> chatIds = databaseService.getChatIdList();
            for (long chatId : chatIds) {
                int day = databaseService.getUserDay(chatId);
                message.setChatId(chatId);
                String[] words = databaseService.getWord(day);
                message.setText(words[0] + " - " + words[1]);
                sender.execute(this.message);

                databaseService.updateUsersDay(chatId, day + 1);
            }
        }
    }
}
