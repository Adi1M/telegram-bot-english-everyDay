package service;

public interface EnglishService {

  //TODO Refactor return to Map<String, String>
  String[] getWord(int day);

  int getUserDay(long userId);

  void updateUsersDay(long userId, int day);
}
