package service.database;

import repository.EnglishRepository;
import repository.EnglishTestRepository;
import repository.UserRepository;

public interface DatabaseService extends UserRepository, EnglishRepository, EnglishTestRepository {
}
