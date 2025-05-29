package telegram_webapp_auth.exception;

public class LocalTunnelStartException extends RuntimeException {
    public LocalTunnelStartException(Throwable cause) {
        super("Ошибка запуска или подключения к LocalTunnel", cause);
    }

    public LocalTunnelStartException(int attempts) {
        super("LocalTunnel не стал доступен после " + attempts + " попыток");
    }

    public LocalTunnelStartException(String message, Throwable cause) {
        super(message, cause);
    }
}