package by.panic.entomus.exception;

public class ApiKeyNotFound extends RuntimeException {
    public ApiKeyNotFound(String exceptionMessage) {
        super(exceptionMessage);
    }
}
