public class Results {
    PostgreSQLJDBS postgreSQLJDBS;

    public Results() {
        postgreSQLJDBS = PostgreSQLJDBS.getInstance();
    }

    public String getLastResult(long chatId) {
        int day = this.postgreSQLJDBS.getUsersDay(chatId);
        int week = day / 7;
        int userResult = postgreSQLJDBS.getLastTestResult(chatId, week - 1);

        if (userResult == -1) {
            return "You didn't take the test last week";
        }else {
            return "Your last test result " + userResult + "/7";
        }
    }

    public String getTotalResult(long chatId){
        return postgreSQLJDBS.getTotalResult(chatId);
    }
}
