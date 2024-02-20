package by.panic.entomus.handler;

import by.panic.entomus.exception.ApiKeyNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiKeyNotFoundAdvancedHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ApiKeyNotFound.class)
    public by.panic.entomus.payload.ExceptionHandler handleApiKeyNotFoundException(ApiKeyNotFound apiKeyNotFound) {
        return by.panic.entomus.payload.ExceptionHandler.builder()
                .state(1)
                .exceptionMessage(apiKeyNotFound.getMessage())
                .build();
    }
}
