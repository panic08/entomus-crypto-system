package by.panic.entomus.service.implement;

import by.panic.entomus.entity.Invoice;
import by.panic.entomus.entity.Merchant;
import by.panic.entomus.enums.InvoiceStatus;
import by.panic.entomus.exception.ApiKeyNotFound;
import by.panic.entomus.exception.PaymentException;
import by.panic.entomus.payload.payment.CreatePaymentQrRequest;
import by.panic.entomus.payload.payment.CreatePaymentRequest;
import by.panic.entomus.payload.payment.CreatePaymentResponse;
import by.panic.entomus.repository.InvoiceRepository;
import by.panic.entomus.repository.MerchantRepository;
import by.panic.entomus.scheduler.CryptoCurrency;
import by.panic.entomus.service.PaymentService;
import by.panic.entomus.util.QrUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final MerchantRepository merchantRepository;
    private final InvoiceRepository invoiceRepository;
    private final CryptoCurrency cryptoCurrency;

    @Override
    public ResponseEntity<byte[]> createQr(String apiKey, CreatePaymentQrRequest createPaymentQrRequest) {
        return null;
    }

    @Transactional
    @Override
    public CreatePaymentResponse create(String apiKey, CreatePaymentRequest createPaymentRequest) {
        if (!merchantRepository.existsByApiKey(apiKey)) {
            throw new ApiKeyNotFound("Incorrect X-API-KEY");
        }

        if (invoiceRepository.existsByOrderId(createPaymentRequest.getOrderId())) {
            throw new PaymentException("Invoice with this order_id already exists");
        }

        switch (createPaymentRequest.getCurrency()) {
            case USD -> {
                if (createPaymentRequest.getAmount() < 0.6 || createPaymentRequest.getAmount() > 100_000) {
                    throw new PaymentException("Minimum 0.6 USD and maximum 100.000 USD for create invoice");
                }
            }
        }

//        Merchant merchant = merchantRepository.findByApiKey(apiKey);
//
//        Invoice invoice = Invoice.builder()
//                .status(InvoiceStatus.PENDING)
//                .uuid(UUID.randomUUID().toString())
//                .orderId(createPaymentRequest.getOrderId())
//                .amount(invoiceAmount)
//
//                .build();
        return null;
    }

//    @Override
//    public byte[] createQr(String apiKey) {
//        File file = null;
//
//        try {
//            file = File.createTempFile("qrc", ".png");
//
//            qrUtil.generateQRCode("", file.getPath(), 300, 300);
//        } catch (IOException | WriterException e) {
//            log.warn(e.getMessage());
//        }
//
//        System.out.println(file.getPath());
//        return new byte[0];
//    }
}
