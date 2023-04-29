package service.auth;

public interface AuthUserService {

    void registerUser(long chatId);

    boolean checkUser(long chatId);

    String getWelcomeText();
}
