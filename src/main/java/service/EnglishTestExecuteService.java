package service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pojo.TestCreator;
import service.database.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class EnglishTestExecuteService {
    private final DatabaseService databaseService;
    private final AbsSender sender;

    EnglishTestExecuteService(AbsSender sender, DatabaseService databaseService) {
        this.sender = sender;
        this.databaseService = databaseService;
    }

    public void foo(long chatId) {
        TestCreator test = new TestCreator(databaseService, chatId);
        String text = "";
        int week = databaseService.getUserDay(chatId) / 7;
        if (!test.isItRightDay()) {
            text = "Today is not for tests just chill!";
        } else if (databaseService.hasTested(chatId, week)) {
            text = "You have tested!";
        } else {
            databaseService.createResult(chatId, week);
            for (int i = 1; i <= 8; i++) {
                if (i < 8) {
                    sendInlineKeyboard(i, chatId,
                            test.getQuestion(i - 1),
                            test.getAnswers(i - 1));
                } else {
                    text = "Test is over! Thank you!";
                }
            }
        }
        try {
            sender.execute(new SendMessage(String.valueOf(chatId), text));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendInlineKeyboard(int index, long chatId, String question, List<String> answers) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(question);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> Buttons = new ArrayList<>();
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
            sender.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
