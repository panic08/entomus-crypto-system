package by.panic.entomus.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MethodArgumentNotValidAdvancedHandler {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public by.panic.entomus.payload.ExceptionHandler handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return processErrors(e);
    }

    private by.panic.entomus.payload.ExceptionHandler processErrors(MethodArgumentNotValidException e) {
        return by.panic.entomus.payload.ExceptionHandler.builder()
                .state(1)
                .exceptionMessage(e.getBindingResult().getFieldError().getDefaultMessage())
                .build();
    }
}
