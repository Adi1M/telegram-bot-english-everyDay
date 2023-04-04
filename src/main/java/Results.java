import service.database.DatabaseService;

public class Results {
    private final DatabaseService databaseService;

    public Results(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public String getLastResult(long chatId) {
        int day = databaseService.getUserDay(chatId);
        int week = day / 7;
        int userResult = databaseService.getLastTestResult(chatId, week - 1);

        if (userResult == -1) {
            return "You didn't take the test last week";
        }else {
            return "Your last test result " + userResult + "/7";
        }
    }

    public String getTotalResult(long chatId){
        return databaseService.getTotalResult(chatId);
    }
}
