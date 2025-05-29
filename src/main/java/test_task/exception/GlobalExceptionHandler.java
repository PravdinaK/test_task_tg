package test_task.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidAuthException.class)
    public ResponseEntity<String> handleInvalidAuth(InvalidAuthException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Авторизация не пройдена: неверные данные");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleOtherExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Внутренняя ошибка сервера");
    }
}