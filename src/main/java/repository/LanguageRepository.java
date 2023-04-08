package repository;

import enums.LangTemplate;

import java.util.List;
import java.util.Map;

public interface LanguageRepository {
  Map<LangTemplate, String> getWord(int day);

  List<Map<LangTemplate, String>> getWordWithRange(Long userId, int range);

  int getUserDay(long userId);

  void updateUsersDay(long userId, int day);

  List<Map<String, Object>> getAllUsersWithDayAndWord();
}
