package service.database;

import service.EnglishService;
import service.EnglishTestService;
import service.UserService;

public interface DatabaseService extends UserService, EnglishService, EnglishTestService {
}
