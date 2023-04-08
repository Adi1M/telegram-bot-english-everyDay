package service.database;

import repository.LanguageRepository;
import repository.EnglishTestRepository;
import repository.UserRepository;

public interface DatabaseService extends UserRepository, LanguageRepository, EnglishTestRepository {
}
