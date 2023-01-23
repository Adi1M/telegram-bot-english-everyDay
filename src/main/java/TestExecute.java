import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class TestExecute extends Thread {

    private final String botUsername;
    private final String botToken;
    private long chatId;

    TestExecute(String botUsername, String botToken,long chatId) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.chatId = chatId;
    }

    @SneakyThrows
    @Override
    public void run() {
        TestService test = new TestService(chatId);
        EnglishForEveryDayBot engBot = new EnglishForEveryDayBot(botUsername,botToken);
        PostgreSQLJDBS postgreSQLJDBS = new PostgreSQLJDBS();
        int week = postgreSQLJDBS.getUsersDay(chatId)/7;
        if(!test.isItRightDay()) {
            SendMessage message = new SendMessage();
            message.setText("Today is not for tests just chill!");
            message.setChatId(chatId);
            try {
                engBot.execute(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(postgreSQLJDBS.hasTested(chatId,week)) {
            SendMessage message = new SendMessage();
            message.setText("You have tested!");
            message.setChatId(chatId);
            try {
                engBot.execute(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            postgreSQLJDBS.insertToResults(chatId,week);
            for(int i = 1; i <= 8; i++) {
                if(i < 8) {
                    sendCustomKeyboard(i, chatId, test.getQuestion(i - 1), test.getAnswers(i - 1));
                }else {
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText("Test is over! Thank you!");
                    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                    List<KeyboardRow> keyboard = new ArrayList<>();
                    keyboardMarkup.setKeyboard(keyboard);
                    message.setReplyMarkup(keyboardMarkup);

                    try {
                        engBot.execute(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                Thread.sleep(10 * 1000);
            }
        }

    }

    public void sendCustomKeyboard(int index, long chatId, String questions, List<String> answers) {
        EnglishForEveryDayBot engBot = new EnglishForEveryDayBot(botUsername,botToken);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(questions);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        for (String answer : answers) {
            row.add(index + " -> " + answer);
        }
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);
        try {
            engBot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

//    public void sendInlineKeyboard(int index, long chatId, String question, List<String> answers) {
//        EnglishForEveryDayBot engBot = new EnglishForEveryDayBot(botUsername,botToken);
//        SendMessage message = new SendMessage();
//        message.setChatId(chatId);
//        message.setText(question);
//
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
//        List<InlineKeyboardButton> Buttons = new ArrayList<>();
//        for (String answer : answers) {
//            Buttons.add(new InlineKeyboardButton(index + " -> " + answer));
//        }
//        keyboard.add(Buttons);
//        inlineKeyboardMarkup.setKeyboard(keyboard);
//        message.setReplyMarkup(inlineKeyboardMarkup);
//
//        try {
//            engBot.execute(message);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
}
