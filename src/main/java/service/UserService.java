package service;

import java.util.List;

public interface UserService {

  void createUser(long userId);

  boolean checkUser(long userId);

  List<Long> getChatIdList();
}
