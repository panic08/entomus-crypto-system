package by.panic.entomus.exception;

public class PaymentException extends RuntimeException {
    public PaymentException(String exceptionMessage) {
        super(exceptionMessage);
    }
}
