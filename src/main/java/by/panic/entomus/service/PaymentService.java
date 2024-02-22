package by.panic.entomus.service;

import by.panic.entomus.payload.payment.CreatePaymentRequest;
import by.panic.entomus.payload.payment.CreatePaymentResponse;
import by.panic.entomus.payload.payment.GetPaymentInfoResponse;
import org.springframework.http.ResponseEntity;

public interface PaymentService {
    ResponseEntity<byte[]> createQr(String apiKey, String invoiceUUID);
    CreatePaymentResponse create(String apiKey, CreatePaymentRequest createPaymentRequest);
    GetPaymentInfoResponse getInfo(String apiKey, String uuid, String orderId);
}
