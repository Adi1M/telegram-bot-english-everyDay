public class Results {
    private final long chatId;
    PostgreSQLJDBS postgreSQLJDBS;

    public Results(long chatId) {
        postgreSQLJDBS = PostgreSQLJDBS.getInstance();
        this.chatId = chatId;
    }

    public String getLastResult() {
        int day = this.postgreSQLJDBS.getUsersDay(this.chatId);
        int week = day / 7;
        int userResult = this.postgreSQLJDBS.lastTestResult(chatId, week - 1);

        if (userResult == -1) {
            return "You didn't take the test last week";
        }else {
            return "Your last test result " + userResult + "/7";
        }
    }

    public String getTotalResult(){
        int day = this.postgreSQLJDBS.getUsersDay(this.chatId);
        int userResult = this.postgreSQLJDBS.getTotalResult(chatId);

        if (userResult == -1){
            return "You didn't take the test";
        }else{
            return "Your total result " + userResult + "/" + day;
        }
    }
}
