package telegram_webapp_auth.exception;

public class UserDataProcessingException extends RuntimeException {
    public UserDataProcessingException(Throwable cause) {
        super("Ошибка обработки данных пользователя Telegram", cause);
    }
}