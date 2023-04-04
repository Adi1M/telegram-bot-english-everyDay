package service;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pojo.TestCreator;
import service.database.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class TestExecuteService extends Thread {
    private final DatabaseService databaseService;
    private final long chatId;
    private final AbsSender sender;
    private final TestCreator test;
    private final SendMessage message;

    TestExecuteService(AbsSender sender, DatabaseService databaseService, long chatId) {
        this.test = new TestCreator(databaseService, chatId);
        this.sender = sender;
        this.databaseService = databaseService;
        this.message = new SendMessage();
        this.chatId = chatId;
    }

    @SneakyThrows
    @Override
    public void run() {
        int week = databaseService.getUserDay(this.chatId) / 7;
        if (!this.test.isItRightDay()) {
            this.message.setText("Today is not for tests just chill!");
            this.message.setChatId(chatId);
            try {
                sender.execute(this.message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (databaseService.hasTested(this.chatId, week)) {
            this.message.setText("You have tested!");
            this.message.setChatId(chatId);
            try {
                sender.execute(this.message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            databaseService.createResult(this.chatId, week);
            for (int i = 1; i <= 8; i++) {
                if (i < 8) {
                    sendInlineKeyboard(i, this.chatId, this.test.getQuestion(i - 1), this.test.getAnswers(i - 1));
                } else {
                    this.message.setChatId(this.chatId);
                    this.message.setText("Test is over! Thank you!");
                    try {
                        sender.execute(this.message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                Thread.sleep(10 * 1000);
            }
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
