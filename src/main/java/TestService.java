import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestService {
    PostgreSQLJDBS postgreSQLJDBS = new PostgreSQLJDBS();
    List<String> answers = new ArrayList<>();
    List<String> correctAnswers = new ArrayList<>();
    List<String> wordsForQuestions = new ArrayList<>();
    long chatID;
    int days;
    int index;


    public TestService(long chatID) {
        this.chatID = chatID;
        this.days = postgreSQLJDBS.getUsersDay(chatID);


        for (int i = days - 6; i <= days; i++) {
            String[] words = postgreSQLJDBS.getWord(i);
            wordsForQuestions.add(words[1]);
            correctAnswers.add(words[0]);
        }
    }

    public boolean isItRightDay() {
        return days % 7 == 0;
    }

    public List<String> getAnswers(int in) {
        if (answers.size() != 0) answers.clear();
        String correctAnswer = correctAnswers.get(in);
        answers.add(correctAnswer);

        int random = (int) (Math.random() * days);
        int i = 0;

        while (i < 2) {
            if (i == 1) random = random + 1;
            if (random != 0) {
                String translate = postgreSQLJDBS.getWord(random)[0];
                if (!translate.equals(correctAnswer)) {
                    answers.add(translate);
                    i++;
                }
            } else {
                random = (int) (Math.random() * days);
            }
        }

        index = answers.indexOf(correctAnswer);

        return answers;
    }

    public int getCorrectAnswer() {
        return index;
    }

    public String getQuestion(int i) {
        return "How is this word translated '" + wordsForQuestions.get(i) + "'?";
    }

}
