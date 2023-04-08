package service.languagetest;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface TelegramLanguageTestService {
    void removeUserTest(Long userId) throws UserTestNotFoundException;

    void startUserTest(Long userId) throws TelegramApiException;

    void getExample(Long userId) throws TelegramApiException;

    String getLastResult(long userId);

    String getTotalResult(long userId);
}
