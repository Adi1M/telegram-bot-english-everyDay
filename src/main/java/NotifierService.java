import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

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
            Thread.sleep(20000);
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
                System.out.println(chatId);
            }
        }
    }
}
