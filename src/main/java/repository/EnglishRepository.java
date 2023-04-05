package repository;

import java.util.List;
import java.util.Map;

public interface EnglishRepository {

  //TODO Refactor return to Map<String, String>
  String[] getWord(int day);

  int getUserDay(long userId);

  void updateUsersDay(long userId, int day);

  List<Map<String, Object>> getAllUsersWithDayAndWord();
}
