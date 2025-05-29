package telegram_webapp_auth.exception;

public class TelegramBotRegistrationException extends RuntimeException {
    public TelegramBotRegistrationException(Throwable cause) {
        super("Ошибка при инициализации Telegram-бота", cause);
    }
}