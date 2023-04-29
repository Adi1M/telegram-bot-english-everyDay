package pojo;

import enums.LangTemplate;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class EnglishUserTest {
    private final Long userId;
    private final List<Map<LangTemplate, String>> wordsListWithInfo;

    public EnglishUserTest(Long userId, List<Map<LangTemplate, String>> wordsListWithInfo) {
        this.userId = userId;
        this.wordsListWithInfo = wordsListWithInfo;
    }
}
