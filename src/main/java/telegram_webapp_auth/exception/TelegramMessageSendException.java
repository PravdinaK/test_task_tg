package telegram_webapp_auth.exception;

public class TelegramMessageSendException extends RuntimeException {
    public TelegramMessageSendException(Throwable cause) {
        super("Не удалось отправить сообщение через Telegram", cause);
    }
}