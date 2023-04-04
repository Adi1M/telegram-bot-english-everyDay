package service.englishtest;

public interface EnglishTestService {

    boolean receiveAnswer(long userId, String answer, int numOfAns);

    void getResultOfAnswer(long userId, String answer);

    String getLastResult(long userId);

    String getTotalResult(long userId);

    void getExample(long userId);
}
