package by.panic.entomus.service.implement;

import by.panic.entomus.api.NodeFactoryApi;
import by.panic.entomus.api.WebHookApi;
import by.panic.entomus.api.payload.nodeFactory.NodeFactoryGetStatusResponse;
import by.panic.entomus.api.payload.nodeFactory.NodeFactoryReceiveRequest;
import by.panic.entomus.api.payload.nodeFactory.NodeFactoryReceiveResponse;
import by.panic.entomus.api.payload.nodeFactory.enums.NodeFactoryGetStatusStatus;
import by.panic.entomus.api.payload.webhook.WebHookRequest;
import by.panic.entomus.api.payload.webhook.enums.WebHookType;
import by.panic.entomus.entity.Invoice;
import by.panic.entomus.entity.Merchant;
import by.panic.entomus.entity.Wallet;
import by.panic.entomus.enums.CryptoToken;
import by.panic.entomus.enums.InvoiceStatus;
import by.panic.entomus.exception.ApiKeyNotFound;
import by.panic.entomus.exception.PaymentException;
import by.panic.entomus.mapper.InvoiceToInvoiceDtoMapperImpl;
import by.panic.entomus.payload.payment.CreatePaymentRequest;
import by.panic.entomus.payload.payment.CreatePaymentResponse;
import by.panic.entomus.payload.payment.GetPaymentInfoResponse;
import by.panic.entomus.repository.InvoiceRepository;
import by.panic.entomus.repository.MerchantRepository;
import by.panic.entomus.repository.WalletRepository;
import by.panic.entomus.scheduler.CryptoCurrency;
import by.panic.entomus.service.PaymentService;
import by.panic.entomus.util.QrUtil;
import by.panic.entomus.util.RounderUtil;
import by.panic.entomus.util.SHA256Util;
import com.google.zxing.WriterException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final MerchantRepository merchantRepository;
    private final InvoiceRepository invoiceRepository;
    private final WalletRepository walletRepository;
    private final InvoiceToInvoiceDtoMapperImpl invoiceToInvoiceDtoMapper;
    private final ExecutorService executorService;
    private final NodeFactoryApi nodeFactoryApi;
    private final WebHookApi webHookApi;
    private final CryptoCurrency cryptoCurrency;
    private final QrUtil qrUtil;
    private final SHA256Util sha256Util;
    private final RounderUtil rounderUtil;

    @Value("${payments.fee}")
    private Double paymentFee;

    @Override
    public ResponseEntity<byte[]> createQr(String apiKey, String invoiceUUID) {
        if (!merchantRepository.existsByApiKey(apiKey)) {
            throw new ApiKeyNotFound("Incorrect X-API-KEY");
        }

        String address = invoiceRepository.findAddressByUuid(invoiceUUID);

        if (address == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] generatedAddress = null;

        try {
            generatedAddress = qrUtil.generateQRCode(address, 256, 256);
        } catch (WriterException | IOException e) {
            log.warn(e.getMessage());
            generatedAddress = null;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(generatedAddress);
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


        Merchant invoiceMerchant = merchantRepository.findByApiKey(apiKey);

        long invoiceTimestamp = System.currentTimeMillis();

        Invoice newInvoice = Invoice.builder()
                .status(InvoiceStatus.PENDING)
                .uuid(existsOnUuid(UUID.randomUUID().toString()))
                .orderId(createPaymentRequest.getOrderId())
                .currency(createPaymentRequest.getCurrency())
                .network(createPaymentRequest.getNetwork())
                .token(createPaymentRequest.getToken())
                .additionalData(createPaymentRequest.getAdditionalData())
                .urlCallback(createPaymentRequest.getUrlCallback())
                .urlReturn(createPaymentRequest.getUrlReturn())
                .urlSuccess(createPaymentRequest.getUrlSuccess())
                .isFinal(false)
                .createdAt(invoiceTimestamp)
                .expiredAt(invoiceTimestamp + (createPaymentRequest.getLifetime() * 1000))
                .merchant(invoiceMerchant)
                .build();

        double newInvoiceAmount = rounderUtil.roundToNDecimalPlaces(createPaymentRequest.getAmount(), 2);

        newInvoice.setAmount(newInvoiceAmount);

        if (createPaymentRequest.getDiscountPercent() != null) {
            double discount = newInvoiceAmount * ((double) createPaymentRequest.getDiscountPercent() / 100);

            newInvoiceAmount -= discount;

            newInvoice.setDiscount(discount);
            newInvoice.setDiscountPercent(createPaymentRequest.getDiscountPercent());
        }

        newInvoice.setPayerAmount(newInvoiceAmount + (newInvoiceAmount * paymentFee));

        BigDecimal newInvoiceMerchantAmountDecimal;
        BigDecimal newInvoiceMerchantAmountWithComsDecimal = null;
        BigInteger newInvoiceMerchantAmount = null;
        BigInteger newInvoiceMerchantAmountWithComs = null;

        switch (newInvoice.getToken()) {
            case ETH -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getEth();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e18));

                newInvoiceMerchantAmountWithComsDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 5);
                newInvoiceMerchantAmountWithComs = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountWithComsDecimal.toBigInteger(),
                        5);
            }

            case BNB -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getBnb();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e18));

                newInvoiceMerchantAmountWithComsDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 5);
                newInvoiceMerchantAmountWithComs = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountWithComsDecimal.toBigInteger(),
                        5);
            }

            case MATIC -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getMatic();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e18));

                newInvoiceMerchantAmountWithComsDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 4);
                newInvoiceMerchantAmountWithComs = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountWithComsDecimal.toBigInteger(),
                        4);
            }

            case ETC -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getEtc();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e18));

                newInvoiceMerchantAmountWithComsDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 5);
                newInvoiceMerchantAmountWithComs = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountWithComsDecimal.toBigInteger(),
                        5);
            }

            case AVAX -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getAvax();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e18));

                newInvoiceMerchantAmountWithComsDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 5);
                newInvoiceMerchantAmountWithComs = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountWithComsDecimal.toBigInteger(),
                        5);
            }

            case SOL -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getSol();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e9));

                newInvoiceMerchantAmountWithComsDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 4);
                newInvoiceMerchantAmountWithComs = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountWithComsDecimal.toBigInteger(),
                        4);
            }

            case BCH -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getBch();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e8));

                newInvoiceMerchantAmountWithComsDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 5);
                newInvoiceMerchantAmountWithComs = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountWithComsDecimal.toBigInteger(),
                        5);
            }

            case LTC -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getLtc();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e8));

                newInvoiceMerchantAmountWithComsDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 5);
                newInvoiceMerchantAmountWithComs = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountWithComsDecimal.toBigInteger(),
                        5);
            }


            case BTC -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getBtc();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e8));

                newInvoiceMerchantAmountWithComsDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 5);
                newInvoiceMerchantAmountWithComs = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountWithComsDecimal.toBigInteger(),
                        5);
            }

            case TRX -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getTrx();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e6));

                newInvoiceMerchantAmountWithComsDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 4);
                newInvoiceMerchantAmountWithComs = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountWithComsDecimal.toBigInteger(),
                        4);
            }


            case USDT -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getUsdt();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e6));

                newInvoiceMerchantAmountWithComsDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 4);
                newInvoiceMerchantAmountWithComs = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountWithComsDecimal.toBigInteger(),
                        4);
            }

            case USDC -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getUsdc();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e6));

                newInvoiceMerchantAmountWithComsDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 4);
                newInvoiceMerchantAmountWithComs = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountWithComsDecimal.toBigInteger(),
                        4);
            }

            case DAI -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getDai();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e18));

                newInvoiceMerchantAmountWithComsDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 4);
                newInvoiceMerchantAmountWithComs = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountWithComsDecimal.toBigInteger(),
                        4);
            }
        }

        newInvoice.setMerchantAmount(existsOnPendingMerchantAmount(newInvoiceMerchantAmountWithComs, newInvoice.getToken()).toString());

        NodeFactoryReceiveResponse nodeFactoryReceiveResponse = nodeFactoryApi.receive(NodeFactoryReceiveRequest.builder()
                        .network(newInvoice.getNetwork())
                        .token(newInvoice.getToken())
                        .amount(newInvoiceMerchantAmount)
                        .timeout(createPaymentRequest.getLifetime() / 60)
                .build());

        if (nodeFactoryReceiveResponse == null) {
            throw new PaymentException("You have entered an invalid Network-Token pair");
        } else {
            newInvoice.setAddress(nodeFactoryReceiveResponse.getAddress());
        }

        newInvoice = invoiceRepository.save(newInvoice);



        Invoice finalNewInvoice = newInvoice;
        BigInteger finalNewInvoiceMerchantAmount = newInvoiceMerchantAmount;
        executorService.submit(() -> {
            boolean isAlreadyFounded = false;

            while (System.currentTimeMillis() < finalNewInvoice.getExpiredAt()) {
                NodeFactoryGetStatusResponse nodeFactoryGetStatusResponse =
                        nodeFactoryApi.getStatus(nodeFactoryReceiveResponse.getId());

                if (nodeFactoryGetStatusResponse.getStatus().equals(NodeFactoryGetStatusStatus.SUCCESS)) {
                    finalNewInvoice.setStatus(InvoiceStatus.SUCCESS);
                    finalNewInvoice.setTxId(nodeFactoryGetStatusResponse.getHash());
                    finalNewInvoice.setIsFinal(true);
                    finalNewInvoice.setUpdatedAt(System.currentTimeMillis());

                    invoiceRepository.save(finalNewInvoice);

                    Wallet wallet = walletRepository.findByNetworkAndTokenAndMerchant(finalNewInvoice.getNetwork(),
                            finalNewInvoice.getToken(), finalNewInvoice.getMerchant());

                    BigInteger walletBalance = new BigInteger(wallet.getBalance());

                    walletBalance = walletBalance.add(finalNewInvoiceMerchantAmount);

                    wallet.setBalance(walletBalance.toString());

                    walletRepository.save(wallet);

                    if (finalNewInvoice.getUrlCallback() != null) {
                        WebHookRequest webHookRequest = WebHookRequest.builder()
                                .type(WebHookType.PAYMENT)
                                .uuid(finalNewInvoice.getUuid())
                                .status(finalNewInvoice.getStatus())
                                .orderId(finalNewInvoice.getOrderId())
                                .amount(finalNewInvoice.getAmount())
                                .paymentAmount(finalNewInvoice.getPayerAmount())
                                .currency(finalNewInvoice.getCurrency())
                                .merchantAmount(finalNewInvoice.getMerchantAmount())
                                .network(finalNewInvoice.getNetwork())
                                .token(finalNewInvoice.getToken())
                                .additionalData(finalNewInvoice.getAdditionalData())
                                .txId(finalNewInvoice.getTxId())
                                .isFinal(finalNewInvoice.getIsFinal())
                                .sign(sha256Util.encodeStringToSHA256(finalNewInvoice.getUuid()
                                + "&" + finalNewInvoice.getOrderId() + "&" + finalNewInvoice.getMerchantAmount()
                                + "&" + finalNewInvoice.getNetwork()  + "&" + finalNewInvoice.getToken() + "&" + apiKey))
                                .build();

                        webHookApi.send(webHookRequest, finalNewInvoice.getUrlCallback());
                    }
                    return;
                } else if (!isAlreadyFounded && nodeFactoryGetStatusResponse.getStatus().equals(NodeFactoryGetStatusStatus.FOUND)) {
                    finalNewInvoice.setStatus(InvoiceStatus.FOUND);
                    finalNewInvoice.setTxId(nodeFactoryGetStatusResponse.getHash());
                    finalNewInvoice.setUpdatedAt(System.currentTimeMillis());

                    invoiceRepository.save(finalNewInvoice);

                    isAlreadyFounded = true;
                }

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            finalNewInvoice.setStatus(InvoiceStatus.EXPIRED);
            finalNewInvoice.setUpdatedAt(System.currentTimeMillis());
            finalNewInvoice.setIsFinal(true);

            invoiceRepository.save(finalNewInvoice);
        });



        return CreatePaymentResponse.builder()
                .state(0)
                .result(invoiceToInvoiceDtoMapper.invoiceToInvoiceDto(newInvoice))
                .build();
    }

    @Override
    public GetPaymentInfoResponse getInfo(String apiKey, String uuid, String orderId) {
        if (!merchantRepository.existsByApiKey(apiKey)) {
            throw new ApiKeyNotFound("Incorrect X-API-KEY");
        }

        if (uuid != null) {
            return GetPaymentInfoResponse.builder()
                    .state(0)
                    .result(invoiceToInvoiceDtoMapper.invoiceToInvoiceDto(invoiceRepository.findByUuid(uuid)))
                    .build();
        } else if (orderId != null) {
            return GetPaymentInfoResponse.builder()
                    .state(0)
                    .result(invoiceToInvoiceDtoMapper.invoiceToInvoiceDto(invoiceRepository.findByOrderId(orderId)))
                    .build();
        }

        throw new PaymentException("Provide uuid or orderId");
    }

    private String existsOnUuid(String uuid) {
        if (invoiceRepository.existsByUuid(uuid)) {
            return existsOnUuid(UUID.randomUUID().toString());
        } else {
            return uuid;
        }
    }

    private BigInteger existsOnPendingMerchantAmount(BigInteger merchantAmount, CryptoToken token) {
        if (invoiceRepository.existsByMerchantAmountAndTokenAndStatus(merchantAmount.toString(), token, InvoiceStatus.PENDING)) {
            switch (token) {
                case ETH -> merchantAmount = merchantAmount.add(BigInteger.valueOf((long) 1e12));

                case ETC, AVAX -> merchantAmount = merchantAmount.add(BigInteger.valueOf((long) 1e14));

                case BNB -> merchantAmount = merchantAmount.add(BigInteger.valueOf((long) 1e13));

                case MATIC, DAI -> merchantAmount = merchantAmount.add(BigInteger.valueOf((long) 1e15));

                case BTC -> merchantAmount = merchantAmount.add(BigInteger.valueOf((long) 1e1));

                case LTC, BCH, USDT, USDC -> merchantAmount = merchantAmount.add(BigInteger.valueOf((long) 1e3));

                case TRX, SOL -> merchantAmount = merchantAmount.add(BigInteger.valueOf((long) 1e4));
            }

            return existsOnPendingMerchantAmount(merchantAmount, token);
        } else {
            return merchantAmount;
        }
    }
}
