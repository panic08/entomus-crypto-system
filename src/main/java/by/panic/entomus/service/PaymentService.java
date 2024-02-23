package by.panic.entomus.service;

import by.panic.entomus.payload.payment.*;
import org.springframework.http.ResponseEntity;

public interface PaymentService {
    ResponseEntity<byte[]> createQr(String apiKey, String invoiceUUID);
    CreatePaymentResponse create(String apiKey, CreatePaymentRequest createPaymentRequest);
    GetPaymentInfoResponse getInfo(String apiKey, String uuid, String orderId);
    GetPaymentHistoryResponse getHistory(String apiKey, Long dateFrom, Long dateTo);
    ResendWebHookResponse resendWebHook(String apiKey, ResendWebHookRequest resendWebhookRequest);
    TestWebHookResponse testWebHook(String apiKey, TestWebHookRequest testWebHookRequest);
    GetPaymentServiceResponse getServices(String apiKey);
}
