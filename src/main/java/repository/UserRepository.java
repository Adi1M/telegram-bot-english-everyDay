package repository;

public interface UserRepository {

  void createUser(long userId);

  boolean checkUser(long userId);
}
