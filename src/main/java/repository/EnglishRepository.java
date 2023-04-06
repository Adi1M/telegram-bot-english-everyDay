package repository;

import enums.LangTemplate;

import java.util.List;
import java.util.Map;

public interface EnglishRepository {
  Map<LangTemplate, String> getWord(int day);

  List<Map<LangTemplate, String>> getWordWithRange(int day, int range);

  int getUserDay(long userId);

  void updateUsersDay(long userId, int day);

  List<Map<String, Object>> getAllUsersWithDayAndWord();
}
