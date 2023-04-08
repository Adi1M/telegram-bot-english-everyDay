package service.languagetest;

import service.database.DatabaseException;
import utils.PropUtils;
import utils.connector.PostgresConnector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.database.DatabaseService;
import service.database.DatabaseServiceImpl;

import java.io.IOException;
import java.util.Properties;

class LanguageTestServiceImplTest {
    private static final String APP_PROP_FILE = "application.properties";
    private static LanguageTestServiceImpl englishTestService;

    @BeforeAll
    static void setUp() throws IOException, DatabaseException {
        Properties properties = PropUtils.getProperties(APP_PROP_FILE);
        DatabaseService databaseService = new DatabaseServiceImpl(
                PostgresConnector.getInstance(properties));
        englishTestService = LanguageTestServiceImpl.getInstance(databaseService);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void receiveAnswer() {
    }

    @Test
    void getResultOfAnswer() {
    }

    @Test
    void getLastResult() {
    }

    @Test
    void getTotalResult() {
    }

    @Test
    void getExample() {
    }
}