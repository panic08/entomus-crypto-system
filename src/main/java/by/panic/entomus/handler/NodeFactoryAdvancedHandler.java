package by.panic.entomus.handler;

import by.panic.entomus.exception.NodeFactoryException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class NodeFactoryAdvancedHandler {
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NodeFactoryException.class)
    public by.panic.entomus.payload.ExceptionHandler handleNodeFactoryException(NodeFactoryException nodeFactoryException) {
        return by.panic.entomus.payload.ExceptionHandler.builder()
                .state(1)
                .exceptionMessage(nodeFactoryException.getMessage())
                .build();
    }
}
