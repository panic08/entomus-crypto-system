package by.panic.entomus.service;

import by.panic.entomus.payload.payment.CreatePaymentQrRequest;
import by.panic.entomus.payload.payment.CreatePaymentRequest;
import by.panic.entomus.payload.payment.CreatePaymentResponse;
import org.springframework.http.ResponseEntity;

public interface PaymentService {
    ResponseEntity<byte[]> createQr(String apiKey, CreatePaymentQrRequest createPaymentQrRequest);
    CreatePaymentResponse create(String apiKey, CreatePaymentRequest createPaymentRequest);
}
