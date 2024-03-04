package by.panic.entomus.service;

import by.panic.entomus.payload.payment.invoice.*;
import by.panic.entomus.payload.payment.staticWallet.*;
import org.springframework.http.ResponseEntity;

public interface PaymentService {
    ResponseEntity<byte[]> createInvoiceQr(String apiKey, String uuid);
    CreatePaymentInvoiceResponse createInvoice(String apiKey, CreatePaymentInvoiceRequest createPaymentInvoiceRequest);
    GetPaymentInvoiceInfoResponse getInvoiceInfo(String apiKey, String uuid, String orderId);
    GetPaymentInvoiceHistoryResponse getInvoiceHistory(String apiKey, Long dateFrom, Long dateTo);
    ResendPaymentInvoiceWebHookResponse resendInvoiceWebHook(String apiKey, ResendPaymentInvoiceWebHookRequest resendPaymentInvoiceWebhookRequest);
    TestPaymentInvoiceWebHookResponse testInvoiceWebHook(String apiKey, TestPaymentInvoiceWebHookRequest testPaymentInvoiceWebHookRequest);
    GetPaymentInvoiceServiceResponse getInvoiceServices(String apiKey);
    CreateStaticWalletResponse createStaticWallet(String apiKey, CreateStaticWalletRequest createStaticWalletRequest);
    BlockStaticWalletResponse blockStaticWallet(String apiKey, BlockStaticWalletRequest blockStaticWalletRequest);
    ResponseEntity<byte[]> createStaticWalletQr(String apiKey, String uuid);
    void handleStaticWalletWebHook(StaticWalletTransactionWebHookRequest staticWalletTransactionWebHookRequest);
}
