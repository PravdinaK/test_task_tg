package telegram_webapp_auth.exception;

public class TelegramSignatureVerificationException extends RuntimeException {
    public TelegramSignatureVerificationException(Throwable cause) {
        super("Ошибка проверки подписи Telegram WebApp", cause);
    }
}