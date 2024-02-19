package by.panic.entomus.service;

import by.panic.entomus.payload.CreatePaymentQrRequest;
import org.springframework.http.ResponseEntity;

public interface PaymentService {
    ResponseEntity<byte[]> createPaymentQr(String apiKey, CreatePaymentQrRequest createPaymentQrRequest);
}
