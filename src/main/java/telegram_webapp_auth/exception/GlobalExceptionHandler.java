package telegram_webapp_auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidAuthException.class)
    public ResponseEntity<String> handleInvalidAuth(InvalidAuthException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(TelegramSignatureVerificationException.class)
    public ResponseEntity<String> handleSignatureVerification(TelegramSignatureVerificationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(TelegramInitDataParseException.class)
    public ResponseEntity<String> handleInitDataParse(TelegramInitDataParseException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(TelegramMessageSendException.class)
    public ResponseEntity<String> handleTelegramSend(TelegramMessageSendException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ex.getMessage());
    }

    @ExceptionHandler(UserDataProcessingException.class)
    public ResponseEntity<String> handleUserDataProcessing(UserDataProcessingException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(TelegramBotRegistrationException.class)
    public ResponseEntity<String> handleBotRegistration(TelegramBotRegistrationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ex.getMessage());
    }

    @ExceptionHandler(LocalTunnelStartException.class)
    public ResponseEntity<String> handleLocalTunnel(LocalTunnelStartException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Внутренняя ошибка сервера");
    }
}