import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.List;

public class TestExecute extends Thread {
    private final PostgreSQLJDBS postgreSQLJDBS;
    private final String botUsername;
    private final String botToken;
    private final long chatId;
    private final EnglishForEveryDayBot engBot;
    private final TestCreator test;
    private final SendMessage message;

    TestExecute(String botUsername, String botToken,long chatId) {
        this.test = new TestCreator(chatId);
        this.engBot = new EnglishForEveryDayBot(botUsername,botToken);
        this.postgreSQLJDBS = PostgreSQLJDBS.getInstance();
        this.message = new SendMessage();
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.chatId = chatId;
    }

    @SneakyThrows
    @Override
    public void run() {
        int week = this.postgreSQLJDBS.getUsersDay(this.chatId)/7;
        if(!this.test.isItRightDay()) {
            this.message.setText("Today is not for tests just chill!");
            this.message.setChatId(chatId);
            try {
                this.engBot.execute(this.message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(this.postgreSQLJDBS.hasTested(this.chatId,week)) {
            this.message.setText("You have tested!");
            this.message.setChatId(chatId);
            try {
                this.engBot.execute(this.message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            this.postgreSQLJDBS.insertToResults(this.chatId,week);
            for(int i = 1; i <= 8; i++) {
                if(i < 8) {
                    sendInlineKeyboard(i, this.chatId, this.test.getQuestion(i - 1), this.test.getAnswers(i - 1));
                }else {
                    this.message.setChatId(this.chatId);
                    this.message.setText("Test is over! Thank you!");
                    try {
                        this.engBot.execute(this.message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                Thread.sleep(10 * 1000);
            }
        }

    }

    public void sendInlineKeyboard(int index, long chatId, String question, List<String> answers) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(question);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> Buttons = new ArrayList<InlineKeyboardButton>();
        for (String answer : answers) {
            InlineKeyboardButton newButton = new InlineKeyboardButton();
            newButton.setText(answer);
            newButton.setCallbackData(index + "." + answer);
            Buttons.add(newButton);
        }
        keyboard.add(Buttons);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            engBot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
