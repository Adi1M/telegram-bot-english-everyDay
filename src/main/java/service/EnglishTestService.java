package service;

public interface EnglishTestService {

  void updateResults(long userId, int week);

  void createResult(long userId, int week);

  boolean hasTested(long userId, int week);

  String getExample(long userId);

  int getLastTestResult(long userId, int week);

  String getTotalResult(long userId);
}
