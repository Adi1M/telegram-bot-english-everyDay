package service.languagetest;

import enums.LangTemplate;

import java.util.List;
import java.util.Map;

public interface LanguageTestService {

    boolean receiveAnswer(long userId, String answer, int numOfAns);

    String getResultOfAnswer(long userId, String answer);

    String getLastResult(long userId);

    String getTotalResult(long userId);

    String getExample(long userId);

    Map<LangTemplate, String> getWord(int day);

    List<Map<LangTemplate, String>> getWordWithRange(Long userId, int range);
}
