package telegram_webapp_auth.exception;

public class TelegramInitDataParseException extends RuntimeException {
    public TelegramInitDataParseException(Throwable cause) {
        super("Ошибка парсинга initData", cause);
    }
}