package repository;

public interface EnglishTestRepository {

  void updateResults(long userId, int week);

  void createResult(long userId, int week);

  boolean hasTested(long userId, int week);

  String getExampleByDay(long userId);

  int getLastTestResult(long userId, int week);

  String getTotalResult(long userId);
}
