package test_task.exception;

public class InvalidAuthException extends RuntimeException {

    public InvalidAuthException(String message) {
        super(message);
    }

}