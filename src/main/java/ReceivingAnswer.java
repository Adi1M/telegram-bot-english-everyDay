
public class ReceivingAnswer {
    PostgreSQLJDBS postgreSQLJDBS;
    private final long chatId;

    ReceivingAnswer(long chatId) {
        postgreSQLJDBS = PostgreSQLJDBS.getInstance();
        this.chatId = chatId;
    }

    public boolean receiveAnswer(String ans,int numOfAns) {
            int day = this.postgreSQLJDBS.getUsersDay(this.chatId);
            int week = day/7;
            String[] words = this.postgreSQLJDBS.getWord(7*(week-1) + numOfAns);
            if(ans.trim().equals(words[0].trim())) {
                this.postgreSQLJDBS.updateResults(this.chatId, week);
                return true;
            }
            return false;
    }
}
