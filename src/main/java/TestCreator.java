import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestCreator {
    private final PostgreSQLJDBS postgreSQLJDBS;
    private List<String> answers;
    private List<String> correctAnswers;
    private List<String> wordsForQuestions;
    private int days;
    private int index;


    public TestCreator(long chatID) {
        this.postgreSQLJDBS = PostgreSQLJDBS.getInstance();
        this.answers = new ArrayList<>();
        this.correctAnswers = new ArrayList<>();
        this.wordsForQuestions = new ArrayList<>();
        this.days = postgreSQLJDBS.getUsersDay(chatID);
        for (int i = days - 6; i <= days; i++) {
            String[] words = this.postgreSQLJDBS.getWord(i);
            this.wordsForQuestions.add(words[1]);
            this.correctAnswers.add(words[0]);
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
                String translate = this.postgreSQLJDBS.getWord(random)[0];
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
