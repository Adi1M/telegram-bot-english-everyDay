package repository;

import java.util.List;

public interface UserRepository {

  void createUser(long userId);

  boolean checkUser(long userId);

  List<Long> getChatIdList();
}
