package telegram_webapp_auth.exception;

public class InvalidAuthException extends RuntimeException {
    public InvalidAuthException() {
        super("Авторизация не пройдена: неверные данные");
    }

    public InvalidAuthException(Throwable cause) {
        super("Авторизация не пройдена: неверные данные", cause);
    }
}