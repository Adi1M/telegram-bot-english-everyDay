
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Slf4j
public class EnglishForEveryDayBot extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    SendMessage message;

    public EnglishForEveryDayBot(String botUsername, String botToken) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.message = new SendMessage();
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageFromUser = update.getMessage().getText();
            if(!messageFromUser.contains("->")) {
                switch (messageFromUser) {
                    case "/start" -> {
                        Registration reg = new Registration();
                        long chatId = update.getMessage().getChatId();

                        if (!reg.checkUser(chatId)) {
                            message.setChatId(chatId);
                            message.setText(reg.text);

                            try {
                                execute(message); 
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }

                            reg.insertInDB(chatId);

                        }
                    }
                    case "/test" -> {
                        long chatId = update.getMessage().getChatId();
                        TestExecute testExecute = new TestExecute(this.botUsername,this.botToken,chatId);
                        testExecute.start();
                        System.out.println("Lox");
                    }
                    case "/example" -> {
                        long chatId = update.getMessage().getChatId();
                        ExampleService exampleService = new ExampleService(botUsername, botToken, chatId);
                        exampleService.getExample();
                    }
                }
            }
        }else if(update.hasCallbackQuery()) {
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            String answer = update.getCallbackQuery().getData();
            int indOfAns = answer.charAt(0) - '0';
            ReceivingAnswer receivingAnsFromPoll = new ReceivingAnswer(chatId);

            if(receivingAnsFromPoll.receiveAnswer(answer.substring(2),indOfAns)){
                message.setText("");
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
