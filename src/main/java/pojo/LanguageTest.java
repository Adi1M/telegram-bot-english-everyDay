package pojo;

import enums.LangTemplate;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;
import service.languagetest.LanguageTestService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static service.languagetest.TelegramLanguageTestServiceImpl.DEFAULT_TEST_RANGE_DAYS;
import static service.languagetest.TelegramLanguageTestServiceImpl.DEFAULT_TEST_WORD_SIZE;

@Getter
public class LanguageTest {
    private final Long userId;
    private final List<Map<LangTemplate, String>> wordQuestionList;
    private final List<String> answeredWord;

    @Setter
    private boolean isSend = false;
    @Setter
    private Message sendMessage;
    private int timerForQuestion = 0;
    private int indexQuestion = 0;

    public LanguageTest(Long userId, List<Map<LangTemplate, String>> wordQuestionList) {
        this.userId = userId;
        this.wordQuestionList = wordQuestionList;
        this.answeredWord = new ArrayList<>(wordQuestionList.size());
    }

    public static LanguageTest generateTest(Long userId, LanguageTestService languageTestService) {
        var words = languageTestService.getWordWithRange(userId, DEFAULT_TEST_RANGE_DAYS);
        initOtherWords(words);
        return new LanguageTest(userId, words);
    }

    private static void initOtherWords(List<Map<LangTemplate, String>> words) {
        //TODO optimize code
        List<String> mainWordList = words.stream()
                .map(word -> word.get(LangTemplate.WORD))
                .map(String::trim)
                .collect(Collectors.toList());
        Random random = new Random();
        StringBuilder includeOtherWords;
        for (Map<LangTemplate, String> wordMap : words) {
            includeOtherWords = new StringBuilder();
            List<String> wordForIncorrect = mainWordList.stream()
                    .filter(w -> !w.equals(wordMap.get(LangTemplate.WORD)))
                    .map(String::trim)
                    .collect(Collectors.toList());
            List<Integer> comeIndexes = new ArrayList<>(DEFAULT_TEST_WORD_SIZE);
            int k = 0;
            while (k < DEFAULT_TEST_WORD_SIZE) {
                Integer index = random.nextInt(wordForIncorrect.size());
                if (!comeIndexes.contains(index)) {
                    comeIndexes.add(k, index);
                } else continue;
                includeOtherWords.append(wordForIncorrect.get(index));
                if (k < DEFAULT_TEST_WORD_SIZE - 1) {
                    includeOtherWords.append(",");
                }
                k++;
            }
            wordMap.put(LangTemplate.OTHER_WORDS, includeOtherWords.toString());
        }
    }

    public Map<LangTemplate, String> getQuestion(int index) {
        return wordQuestionList.get(index);
    }

    public void plusQuestionIndex() {
        indexQuestion += 1;
    }

    public void addQuestionSecond() {
        timerForQuestion += 1;
    }

    public void resetTimer() {
        timerForQuestion = 0;
    }
}
