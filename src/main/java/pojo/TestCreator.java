package pojo;

import enums.LangTemplate;
import service.database.DatabaseService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestCreator {
    private final static int DEFAULT_TEST_RANGE_DAYS = 7;
    private final DatabaseService databaseService;
    private final List<String> answers;
    private final List<String> correctAnswers;
    private final List<String> wordsForQuestions;
    private final int days;
    private int index;


    public TestCreator(DatabaseService databaseService, long chatID) {
        this.databaseService = databaseService;
        this.answers = new ArrayList<>();
        this.correctAnswers = new ArrayList<>();
        this.wordsForQuestions = new ArrayList<>();
        this.days = databaseService.getUserDay(chatID);
        //FIXME send error when user come firstly
        // like 1 day - 6 = -5
        List<Map<LangTemplate, String>> words;
        if (days < 7) {
            words = databaseService.getWordWithRange(0, days);
        } else {
            words = databaseService.getWordWithRange(days, DEFAULT_TEST_RANGE_DAYS);
        }
        for (Map<LangTemplate, String> word : words) {
            this.wordsForQuestions.add(word.get(LangTemplate.WORD));
            this.correctAnswers.add(word.get(LangTemplate.TARGET));
        }
    }

    public boolean isItRightDay() {
        return this.days % 7 == 0;
    }

    public List<String> getAnswers(int in) {
        if (this.answers.size() != 0) this.answers.clear();
        String correctAnswer = this.correctAnswers.get(in);
        this.answers.add(correctAnswer);

        int random = (int) (Math.random() * this.days);
        int i = 0;

        while (i < 2) {
            if (i == 1) random = random + 1;
            if (random != 0) {
                String translate = databaseService.getWord(random).get(LangTemplate.TARGET);
                if (!translate.equals(correctAnswer)) {
                    this.answers.add(translate);
                    i++;
                }
            } else {
                random = (int) (Math.random() * days);
            }
        }
        Collections.shuffle(this.answers);
        this.index = this.answers.indexOf(correctAnswer);

        return this.answers;
    }

    public String getQuestion(int i) {
        return "How is this word translated '" + this.wordsForQuestions.get(i) + "'?";
    }
}
