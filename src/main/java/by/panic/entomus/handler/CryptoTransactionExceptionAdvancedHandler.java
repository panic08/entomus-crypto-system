package by.panic.entomus.handler;

import by.panic.entomus.exception.PaymentException;
import by.panic.entomus.exception.PayoutException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CryptoTransactionExceptionAdvancedHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PaymentException.class)
    public by.panic.entomus.payload.ExceptionHandler handlePaymentException(PaymentException paymentException) {
        return by.panic.entomus.payload.ExceptionHandler.builder()
                .state(1)
                .exceptionMessage(paymentException.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PayoutException.class)
    public by.panic.entomus.payload.ExceptionHandler handlePayoutException(PayoutException payoutException) {
        return by.panic.entomus.payload.ExceptionHandler.builder()
                .state(1)
                .exceptionMessage(payoutException.getMessage())
                .build();
    }
}
