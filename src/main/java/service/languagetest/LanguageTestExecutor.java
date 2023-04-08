package service.languagetest;

import enums.LangTemplate;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import pojo.LanguageTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static service.languagetest.TelegramLanguageTestServiceImpl.DEFAULT_TEST_QUESTION_SECONDS;

@Slf4j
public class LanguageTestExecutor {
    private final List<LanguageTest> userTestList;
    private final AbsSender sender;
    private ScheduledExecutorService executor;
    private List<Runnable> stoppedExecuteList;

    public LanguageTestExecutor(AbsSender sender, List<LanguageTest> userTestList) {
        this.sender = sender;
        this.userTestList = userTestList;
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public void start() {
        if (userTestList.isEmpty()) {
            stoppedExecuteList = executor.shutdownNow();
            return;
        }
        if (executor.isShutdown() || executor.isTerminated()) {
            if (!userTestList.isEmpty()) {
                executor = Executors.newSingleThreadScheduledExecutor();
                for (Runnable task : stoppedExecuteList) {
                    executor.schedule(task, 1, TimeUnit.SECONDS);
                }
            }
            return;
        }
        executor.scheduleAtFixedRate(() -> {
                    if (userTestList.isEmpty()) {
                        log.info("User test list IS EMPTY");
                        return;
                    }
                    synchronized (userTestList) {
                        try {
                            for (LanguageTest userTest : userTestList) {
                                //TODO In this place u can add other functionality
                                // maybe we can realize Photo word implementation

                                // if service is not send message
                                if (!userTest.isSend()) {
                                    // Sending message to user, and getting sent message information
                                    // need get message id for updating and deleting
                                    Message message = sendQuestion(userTest.getUserId(), userTest);
                                    log.info("Message for {}", userTest.getUserId());
                                    // updating test config
                                    //TODO need to realize this code in object class LanguageUserTest
                                    // cause in this place we can forgot something
                                    userTest.setSend(true);
                                    userTest.setSendMessage(message);
                                    userTest.plusQuestionIndex();
                                }
                                if (userTest.getTimerForQuestion() == DEFAULT_TEST_QUESTION_SECONDS) {
                                    //TODO need to realize this code in object class LanguageUserTest
                                    // cause in this place we can forgot something
                                    userTest.resetTimer();
                                    userTest.setSend(false);
                                    // todo It will be place here
                                    removeMessage(userTest);
                                }
                                userTest.addQuestionSecond();
                            }
                            //FIXME need to remove user test when service send all questions to user
                            // and waited DEFAULT_TEST_QUESTION_SECONDS
                            // Realize code like this
                            // it will be better that array will remove all user test when (CONDITION)
                            userTestList.removeIf(test ->
                                    test.getIndexQuestion() > test.getWordQuestionList().size()
                            );
                        } catch (TelegramApiException e) {
                            //TODO need to save exception and send message to all user
                            // who tested, and offer to take test again
                            log.error("Telegram exception, when test service send to users test question", e);
                            throw new RuntimeException(e);
                        }
                    }
                }, 0, 1,
                TimeUnit.SECONDS);
    }

    private void removeMessage(LanguageTest userTest) throws TelegramApiException {
        // break pointer
        if (userTest == null || userTest.getSendMessage() == null) {
            return;
        }
        Integer messageId = userTest.getSendMessage().getMessageId();
        sender.execute(new DeleteMessage(
                String.valueOf(userTest.getUserId()),
                messageId));
        log.info("User {} | Message id '{}' deleted", userTest.getUserId(), messageId);
        userTest.setSendMessage(null);
    }

    private Message sendQuestion(long chatId, LanguageTest userTest)
            throws TelegramApiException {
        var question = userTest.getQuestion(userTest.getIndexQuestion());
        String text = String.format("Question %d. %s", userTest.getIndexQuestion(),
                question.get(LangTemplate.TARGET));
        SendMessage message = new SendMessage(String.valueOf(chatId), text);
        List<String> answers = resolveWordsToAnswers(question);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        InlineKeyboardButton button;
        for (String answer : answers) {
            button = new InlineKeyboardButton();
            button.setText(answer);
//            TODO need to send Callbacks
            button.setCallbackData("test");
            buttons.add(button);
        }
        keyboard.add(buttons);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(inlineKeyboardMarkup);

        return sender.execute(message);
    }

    private static List<String> resolveWordsToAnswers(Map<LangTemplate, String> question) {
        List<String> answers = new ArrayList<>();
        answers.add(question.get(LangTemplate.WORD));
        //TODO separator need to place in constant
        answers.addAll(Arrays.asList(question.get(LangTemplate.OTHER_WORDS).split(",")));
        return answers;
    }
}
