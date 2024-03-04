package by.panic.entomus.service.implement;

import by.panic.entomus.api.NodeFactoryApi;
import by.panic.entomus.api.CryptoTransactionWebHookApi;
import by.panic.entomus.api.payload.nodeFactory.NodeFactoryCreateStaticWalletRequest;
import by.panic.entomus.api.payload.nodeFactory.NodeFactoryGetStatusResponse;
import by.panic.entomus.api.payload.nodeFactory.NodeFactoryReceiveRequest;
import by.panic.entomus.api.payload.nodeFactory.NodeFactoryReceiveResponse;
import by.panic.entomus.api.payload.nodeFactory.enums.NodeFactoryGetStatusStatus;
import by.panic.entomus.api.payload.cryptoTransactionWebhook.PaymentWebHookRequest;
import by.panic.entomus.api.payload.cryptoTransactionWebhook.enums.CryptoTransactionWebHookType;
import by.panic.entomus.entity.Invoice;
import by.panic.entomus.entity.Merchant;
import by.panic.entomus.entity.StaticWallet;
import by.panic.entomus.entity.Wallet;
import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import by.panic.entomus.entity.enums.InvoiceStatus;
import by.panic.entomus.entity.enums.StaticWalletStatus;
import by.panic.entomus.exception.PaymentException;
import by.panic.entomus.mapper.InvoiceToInvoiceDtoMapperImpl;
import by.panic.entomus.mapper.StaticWalletToStaticWalletDtoMapperImpl;
import by.panic.entomus.payload.payment.invoice.*;
import by.panic.entomus.payload.payment.staticWallet.*;
import by.panic.entomus.repository.InvoiceRepository;
import by.panic.entomus.repository.MerchantRepository;
import by.panic.entomus.repository.StaticWalletRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final MerchantRepository merchantRepository;
    private final InvoiceRepository invoiceRepository;
    private final WalletRepository walletRepository;
    private final StaticWalletRepository staticWalletRepository;
    private final NodeFactoryApi nodeFactoryApi;
    private final CryptoTransactionWebHookApi cryptoTransactionWebHookApi;
    private final InvoiceToInvoiceDtoMapperImpl invoiceToInvoiceDtoMapper;
    private final StaticWalletToStaticWalletDtoMapperImpl staticWalletToStaticWalletDtoMapper;
    private final CryptoCurrency cryptoCurrency;
    private final ExecutorService executorService;
    private final QrUtil qrUtil;
    private final SHA256Util sha256Util;
    private final RounderUtil rounderUtil;

    @Value("${payments.fee}")
    private Double paymentFee;

    @Value("${staticWallets.url}")
    private String staticWalletsUrl;

    @Override
    public ResponseEntity<byte[]> createInvoiceQr(String apiKey, String uuid) {
        String address = invoiceRepository.findAddressByUuid(uuid);

        if (address == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] generatedAddress = null;

        try {
            generatedAddress = qrUtil.generateTextQRCode(address, 256, 256);
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
    public CreatePaymentInvoiceResponse createInvoice(String apiKey, CreatePaymentInvoiceRequest createPaymentInvoiceRequest) {
        if (invoiceRepository.existsByOrderId(createPaymentInvoiceRequest.getOrderId())) {
            throw new PaymentException("Payment with this order_id already exists");
        }

        switch (createPaymentInvoiceRequest.getNetwork()) {
            case ETH -> {
                switch (createPaymentInvoiceRequest.getCurrency()) {
                    case USD -> {
                        if (createPaymentInvoiceRequest.getAmount() < 15) {
                            throw new PaymentException("Minimum 15 USD for this network for create payment");
                        }
                    }
                }
            }

            case ETC, LTC, AVAX, BCH, SOL, TRX, POLYGON -> {
                switch (createPaymentInvoiceRequest.getCurrency()) {
                    case USD -> {
                        if (createPaymentInvoiceRequest.getAmount() < 1.1) {
                            throw new PaymentException("Minimum 1.1 USD for this network for create payment");
                        }
                    }
                }
            }

            case BTC -> {
                switch (createPaymentInvoiceRequest.getCurrency()) {
                    case USD -> {
                        if (createPaymentInvoiceRequest.getAmount() < 10) {
                            throw new PaymentException("Minimum 10 USD for this network for create payment");
                        }
                    }
                }
            }

            case BNB -> {
                switch (createPaymentInvoiceRequest.getCurrency()) {
                    case USD -> {
                        if (createPaymentInvoiceRequest.getAmount() < 5) {
                            throw new PaymentException("Minimum 5 USD for this network for create payment");
                        }
                    }
                }
            }
        }


        Merchant invoiceMerchant = merchantRepository.findByApiKey(apiKey);

        long invoiceTimestamp = System.currentTimeMillis();

        Invoice newInvoice = Invoice.builder()
                .status(InvoiceStatus.PENDING)
                .uuid(existsOnInvoiceUuid(UUID.randomUUID().toString()))
                .orderId(createPaymentInvoiceRequest.getOrderId())
                .currency(createPaymentInvoiceRequest.getCurrency())
                .network(createPaymentInvoiceRequest.getNetwork())
                .token(createPaymentInvoiceRequest.getToken())
                .additionalData(createPaymentInvoiceRequest.getAdditionalData())
                .urlCallback(createPaymentInvoiceRequest.getUrlCallback())
                .urlReturn(createPaymentInvoiceRequest.getUrlReturn())
                .urlSuccess(createPaymentInvoiceRequest.getUrlSuccess())
                .isFinal(false)
                .createdAt(invoiceTimestamp)
                .expiredAt(invoiceTimestamp + (createPaymentInvoiceRequest.getLifetime() * 1000))
                .merchant(invoiceMerchant)
                .build();

        double newInvoiceAmount = rounderUtil.roundToNDecimalPlaces(createPaymentInvoiceRequest.getAmount(), 2);

        newInvoice.setAmount(newInvoiceAmount);

        if (createPaymentInvoiceRequest.getDiscountPercent() != null) {
            double discount = newInvoiceAmount * ((double) createPaymentInvoiceRequest.getDiscountPercent() / 100);

            newInvoiceAmount -= discount;

            newInvoice.setDiscount(discount);
            newInvoice.setDiscountPercent(createPaymentInvoiceRequest.getDiscountPercent());
        }

        newInvoice.setPayerAmount(newInvoiceAmount + (newInvoiceAmount * paymentFee));

        BigDecimal newInvoiceMerchantAmountDecimal;
        BigDecimal newInvoicePaymentAmountDecimal = null;
        BigInteger newInvoiceMerchantAmount = null;
        BigInteger newInvoicePaymentAmount = null;

        switch (newInvoice.getToken()) {
            case ETH -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentInvoiceRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getEth();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e18));

                newInvoicePaymentAmountDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 5);
                newInvoicePaymentAmount = rounderUtil.replaceDigitsWithZero(newInvoicePaymentAmountDecimal.toBigInteger(),
                        5);
            }

            case BNB -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentInvoiceRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getBnb();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e18));

                newInvoicePaymentAmountDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 5);
                newInvoicePaymentAmount = rounderUtil.replaceDigitsWithZero(newInvoicePaymentAmountDecimal.toBigInteger(),
                        5);
            }

            case MATIC -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentInvoiceRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getMatic();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e18));

                newInvoicePaymentAmountDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 4);
                newInvoicePaymentAmount = rounderUtil.replaceDigitsWithZero(newInvoicePaymentAmountDecimal.toBigInteger(),
                        4);
            }

            case ETC -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentInvoiceRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getEtc();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e18));

                newInvoicePaymentAmountDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 5);
                newInvoicePaymentAmount = rounderUtil.replaceDigitsWithZero(newInvoicePaymentAmountDecimal.toBigInteger(),
                        5);
            }

            case AVAX -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentInvoiceRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getAvax();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e18));

                newInvoicePaymentAmountDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 5);
                newInvoicePaymentAmount = rounderUtil.replaceDigitsWithZero(newInvoicePaymentAmountDecimal.toBigInteger(),
                        5);
            }

            case SOL -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentInvoiceRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getSol();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e9));

                newInvoicePaymentAmountDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 4);
                newInvoicePaymentAmount = rounderUtil.replaceDigitsWithZero(newInvoicePaymentAmountDecimal.toBigInteger(),
                        4);
            }

            case BCH -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentInvoiceRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getBch();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e8));

                newInvoicePaymentAmountDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 5);
                newInvoicePaymentAmount = rounderUtil.replaceDigitsWithZero(newInvoicePaymentAmountDecimal.toBigInteger(),
                        5);
            }

            case LTC -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentInvoiceRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getLtc();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e8));

                newInvoicePaymentAmountDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 5);
                newInvoicePaymentAmount = rounderUtil.replaceDigitsWithZero(newInvoicePaymentAmountDecimal.toBigInteger(),
                        5);
            }


            case BTC -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentInvoiceRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getBtc();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e8));

                newInvoicePaymentAmountDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 5);
                newInvoicePaymentAmount = rounderUtil.replaceDigitsWithZero(newInvoicePaymentAmountDecimal.toBigInteger(),
                        5);
            }

            case TRX -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentInvoiceRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getTrx();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e6));

                newInvoicePaymentAmountDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 4);
                newInvoicePaymentAmount = rounderUtil.replaceDigitsWithZero(newInvoicePaymentAmountDecimal.toBigInteger(),
                        4);
            }


            case USDT -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentInvoiceRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getUsdt();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e6));

                newInvoicePaymentAmountDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 4);
                newInvoicePaymentAmount = rounderUtil.replaceDigitsWithZero(newInvoicePaymentAmountDecimal.toBigInteger(),
                        4);
            }

            case USDC -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentInvoiceRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getUsdc();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e6));

                newInvoicePaymentAmountDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 4);
                newInvoicePaymentAmount = rounderUtil.replaceDigitsWithZero(newInvoicePaymentAmountDecimal.toBigInteger(),
                        4);
            }

            case DAI -> {
                double cryptoForOneDollar = 0d;

                switch (createPaymentInvoiceRequest.getCurrency()) {
                    case USD -> cryptoForOneDollar = 1 / cryptoCurrency.getUsd().getDai();
                }

                double cryptoForNewInvoiceAmount = cryptoForOneDollar * newInvoiceAmount;

                newInvoiceMerchantAmountDecimal = BigDecimal.valueOf(cryptoForNewInvoiceAmount);

                newInvoiceMerchantAmountDecimal = newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf((long) 1e18));

                newInvoicePaymentAmountDecimal =
                        newInvoiceMerchantAmountDecimal.add(newInvoiceMerchantAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)));

                newInvoiceMerchantAmount = rounderUtil.replaceDigitsWithZero(newInvoiceMerchantAmountDecimal.toBigInteger(), 4);
                newInvoicePaymentAmount = rounderUtil.replaceDigitsWithZero(newInvoicePaymentAmountDecimal.toBigInteger(),
                        4);
            }
        }
        newInvoice.setMerchantAmount(newInvoiceMerchantAmount.toString());
//        newInvoice.setMerchantAmount(existsOnPendingMerchantAmount(newInvoiceMerchantAmountWithComs, newInvoice.getToken()).toString());
        newInvoice.setPaymentAmount(existsOnInvoicePendingPaymentAmount(newInvoicePaymentAmount, newInvoice.getToken()).toString());

        NodeFactoryReceiveResponse nodeFactoryReceiveResponse = nodeFactoryApi.receive(NodeFactoryReceiveRequest.builder()
                .network(newInvoice.getNetwork())
                .token(newInvoice.getToken())
                .amount(new BigInteger(newInvoice.getPaymentAmount()))
                .timeout(createPaymentInvoiceRequest.getLifetime() / 60)
                .build());

        newInvoice.setAddress(nodeFactoryReceiveResponse.getAddress());

        newInvoice = invoiceRepository.save(newInvoice);


        Invoice finalNewInvoice = newInvoice;
        BigInteger finalNewInvoiceMerchantAmount = newInvoiceMerchantAmount;
        executorService.submit(() -> {
            boolean isAlreadyFounded = false;

            while (System.currentTimeMillis() < finalNewInvoice.getExpiredAt()) {
                NodeFactoryGetStatusResponse nodeFactoryGetStatusResponse =
                        nodeFactoryApi.getStatus(nodeFactoryReceiveResponse.getId());

                if (!nodeFactoryGetStatusResponse.getStatus().equals(NodeFactoryGetStatusStatus.SUCCESS)) {
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
                        PaymentWebHookRequest webHookRequest = PaymentWebHookRequest.builder()
                                .type(CryptoTransactionWebHookType.PAYMENT)
                                .uuid(finalNewInvoice.getUuid())
                                .status(finalNewInvoice.getStatus())
                                .orderId(finalNewInvoice.getOrderId())
                                .amount(finalNewInvoice.getAmount())
                                .payerAmount(finalNewInvoice.getPayerAmount())
                                .paymentAmount(finalNewInvoice.getPaymentAmount())
                                .currency(finalNewInvoice.getCurrency())
                                .merchantAmount(finalNewInvoice.getMerchantAmount())
                                .network(finalNewInvoice.getNetwork())
                                .token(finalNewInvoice.getToken())
                                .additionalData(finalNewInvoice.getAdditionalData())
                                .txId(finalNewInvoice.getTxId())
                                .isFinal(finalNewInvoice.getIsFinal())
                                .sign(sha256Util.encodeStringToSHA256(finalNewInvoice.getUuid()
                                        + "&" + finalNewInvoice.getOrderId() + "&" + finalNewInvoice.getPaymentAmount()
                                        + "&" + finalNewInvoice.getNetwork()  + "&" + finalNewInvoice.getToken() + "&" + apiKey))
                                .build();

                        cryptoTransactionWebHookApi.sendPayment(webHookRequest, finalNewInvoice.getUrlCallback());
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



        return CreatePaymentInvoiceResponse.builder()
                .state(0)
                .result(invoiceToInvoiceDtoMapper.invoiceToInvoiceDto(newInvoice))
                .build();
    }

    @Override
    public GetPaymentInvoiceInfoResponse getInvoiceInfo(String apiKey, String uuid, String orderId) {
        Merchant principalMerchant = merchantRepository.findByApiKey(apiKey);

        Invoice principalInvoice;

        if (invoiceRepository.existsByUuidAndMerchant(uuid, principalMerchant)) {
            principalInvoice = invoiceRepository.findByUuid(uuid);
        } else if (invoiceRepository.existsByOrderIdAndMerchant(orderId, principalMerchant)) {
            principalInvoice = invoiceRepository.findByOrderId(orderId);
        } else {
            throw new PaymentException("You do not have a invoice with that uuid or order_id");
        }

        return GetPaymentInvoiceInfoResponse.builder()
                .state(0)
                .result(invoiceToInvoiceDtoMapper.invoiceToInvoiceDto(principalInvoice))
                .build();
    }

    @Override
    public GetPaymentInvoiceHistoryResponse getInvoiceHistory(String apiKey, Long dateFrom, Long dateTo) {
        if (dateFrom == null) {
            dateFrom = 0L;
        }

        if (dateTo == null) {
            dateTo = 9999999999999L;
        }

        Merchant principalMerchant = merchantRepository.findByApiKey(apiKey);

        List<Invoice> filteredInvoiceList = invoiceRepository.findAllByMerchantDateFilterOrderByCreatedAtAsc(principalMerchant,
                dateFrom, dateTo);

        return GetPaymentInvoiceHistoryResponse.builder()
                .state(0)
                .result(GetPaymentInvoiceHistoryResponse.Result.builder()
                        .items(invoiceToInvoiceDtoMapper.invoiceListToInvoiceDtoList(filteredInvoiceList))
                        .build())
                .build();
    }

    @Override
    public ResendPaymentInvoiceWebHookResponse resendInvoiceWebHook(String apiKey, ResendPaymentInvoiceWebHookRequest resendPaymentInvoiceWebhookRequest) {
        Merchant principalMerchant = merchantRepository.findByApiKey(apiKey);

        Invoice principalInvoice;

        if (invoiceRepository.existsByUuidAndMerchant(resendPaymentInvoiceWebhookRequest.getInvoiceUuid(), principalMerchant)) {
            principalInvoice = invoiceRepository.findByUuid(resendPaymentInvoiceWebhookRequest.getInvoiceUuid());
        } else if (invoiceRepository.existsByOrderIdAndMerchant(resendPaymentInvoiceWebhookRequest.getOrderId(), principalMerchant)) {
            principalInvoice = invoiceRepository.findByOrderId(resendPaymentInvoiceWebhookRequest.getOrderId());
        } else {
            throw new PaymentException("You do not have a invoice with that uuid or order_id");
        }

        if (principalInvoice == null) {
            throw new PaymentException("Payment not found");
        }

        if (!principalInvoice.getStatus().equals(InvoiceStatus.SUCCESS)) {
            throw new PaymentException("Payment has not yet received \"SUCCESS\" status");
        }

        if (principalInvoice.getUrlCallback() == null) {
            throw new PaymentException("Payment does not specify url_callback");
        }

        PaymentWebHookRequest webHookRequest = PaymentWebHookRequest.builder()
                .type(CryptoTransactionWebHookType.PAYMENT)
                .uuid(principalInvoice.getUuid())
                .status(principalInvoice.getStatus())
                .orderId(principalInvoice.getOrderId())
                .amount(principalInvoice.getAmount())
                .paymentAmount(principalInvoice.getPaymentAmount())
                .payerAmount(principalInvoice.getPayerAmount())
                .currency(principalInvoice.getCurrency())
                .merchantAmount(principalInvoice.getMerchantAmount())
                .network(principalInvoice.getNetwork())
                .token(principalInvoice.getToken())
                .additionalData(principalInvoice.getAdditionalData())
                .txId(principalInvoice.getTxId())
                .isFinal(principalInvoice.getIsFinal())
                .sign(sha256Util.encodeStringToSHA256(principalInvoice.getUuid()
                        + "&" + principalInvoice.getOrderId() + "&" + principalInvoice.getPaymentAmount()
                        + "&" + principalInvoice.getNetwork()  + "&" + principalInvoice.getToken() + "&" + apiKey))
                .build();

        cryptoTransactionWebHookApi.sendPayment(webHookRequest, principalInvoice.getUrlCallback());

        return ResendPaymentInvoiceWebHookResponse.builder()
                .state(0)
                .result(new Object[]{})
                .build();
    }

    @Override
    public TestPaymentInvoiceWebHookResponse testInvoiceWebHook(String apiKey, TestPaymentInvoiceWebHookRequest testPaymentInvoiceWebHookRequest) {
        if (!testPaymentInvoiceWebHookRequest.getStatus().equals(InvoiceStatus.SUCCESS)) {
            throw new PaymentException("Payment has not yet received \"SUCCESS\" status");
        }

        PaymentWebHookRequest webHookRequest = PaymentWebHookRequest.builder()
                .type(CryptoTransactionWebHookType.PAYMENT)
                .uuid(testPaymentInvoiceWebHookRequest.getUuid())
                .status(testPaymentInvoiceWebHookRequest.getStatus())
                .orderId(testPaymentInvoiceWebHookRequest.getOrderId())
                .paymentAmount(testPaymentInvoiceWebHookRequest.getPaymentAmount())
                .network(testPaymentInvoiceWebHookRequest.getNetwork())
                .token(testPaymentInvoiceWebHookRequest.getToken())
                .sign(sha256Util.encodeStringToSHA256(testPaymentInvoiceWebHookRequest.getUuid()
                        + "&" + testPaymentInvoiceWebHookRequest.getOrderId() + "&" + testPaymentInvoiceWebHookRequest.getPaymentAmount()
                        + "&" + testPaymentInvoiceWebHookRequest.getNetwork()  + "&" + testPaymentInvoiceWebHookRequest.getToken() + "&" + apiKey))
                .build();

        cryptoTransactionWebHookApi.sendPayment(webHookRequest, testPaymentInvoiceWebHookRequest.getUrlCallback());

        return TestPaymentInvoiceWebHookResponse.builder()
                .state(0)
                .result(new Object[]{})
                .build();
    }

    @Override
    public GetPaymentInvoiceServiceResponse getInvoiceServices(String apiKey) {
        double percentPaymentFee = paymentFee * 100;
        List<GetPaymentInvoiceServiceResponse.Result> servicesResultList = new ArrayList<>();

        //ETH
        servicesResultList.add(GetPaymentInvoiceServiceResponse.Result.builder()
                .network(CryptoNetwork.ETH)
                .token(CryptoToken.ETH)
                .isAvailable(true)
                .commission(GetPaymentInvoiceServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPaymentFee)
                        .build())
                .build());

        servicesResultList.add(GetPaymentInvoiceServiceResponse.Result.builder()
                .network(CryptoNetwork.ETH)
                .token(CryptoToken.USDT)
                .isAvailable(true)
                .commission(GetPaymentInvoiceServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPaymentFee)
                        .build())
                .build());

        servicesResultList.add(GetPaymentInvoiceServiceResponse.Result.builder()
                .network(CryptoNetwork.ETH)
                .token(CryptoToken.USDC)
                .isAvailable(true)
                .commission(GetPaymentInvoiceServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPaymentFee)
                        .build())
                .build());

        servicesResultList.add(GetPaymentInvoiceServiceResponse.Result.builder()
                .network(CryptoNetwork.ETH)
                .token(CryptoToken.DAI)
                .isAvailable(true)
                .commission(GetPaymentInvoiceServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPaymentFee)
                        .build())
                .build());

        //BTC
        servicesResultList.add(GetPaymentInvoiceServiceResponse.Result.builder()
                .network(CryptoNetwork.BTC)
                .token(CryptoToken.BTC)
                .isAvailable(true)
                .commission(GetPaymentInvoiceServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPaymentFee)
                        .build())
                .build());

        //TRX
        servicesResultList.add(GetPaymentInvoiceServiceResponse.Result.builder()
                .network(CryptoNetwork.TRX)
                .token(CryptoToken.TRX)
                .isAvailable(true)
                .commission(GetPaymentInvoiceServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPaymentFee)
                        .build())
                .build());

        servicesResultList.add(GetPaymentInvoiceServiceResponse.Result.builder()
                .network(CryptoNetwork.TRX)
                .token(CryptoToken.USDT)
                .isAvailable(true)
                .commission(GetPaymentInvoiceServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPaymentFee)
                        .build())
                .build());

        servicesResultList.add(GetPaymentInvoiceServiceResponse.Result.builder()
                .network(CryptoNetwork.TRX)
                .token(CryptoToken.USDC)
                .isAvailable(true)
                .commission(GetPaymentInvoiceServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPaymentFee)
                        .build())
                .build());

        //BNB
        servicesResultList.add(GetPaymentInvoiceServiceResponse.Result.builder()
                .network(CryptoNetwork.BNB)
                .token(CryptoToken.BNB)
                .isAvailable(true)
                .commission(GetPaymentInvoiceServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPaymentFee)
                        .build())
                .build());

        servicesResultList.add(GetPaymentInvoiceServiceResponse.Result.builder()
                .network(CryptoNetwork.BNB)
                .token(CryptoToken.USDT)
                .isAvailable(true)
                .commission(GetPaymentInvoiceServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPaymentFee)
                        .build())
                .build());

        servicesResultList.add(GetPaymentInvoiceServiceResponse.Result.builder()
                .network(CryptoNetwork.BNB)
                .token(CryptoToken.USDC)
                .isAvailable(true)
                .commission(GetPaymentInvoiceServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPaymentFee)
                        .build())
                .build());

        servicesResultList.add(GetPaymentInvoiceServiceResponse.Result.builder()
                .network(CryptoNetwork.BNB)
                .token(CryptoToken.DAI)
                .isAvailable(true)
                .commission(GetPaymentInvoiceServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPaymentFee)
                        .build())
                .build());

        //MATIC
        servicesResultList.add(GetPaymentInvoiceServiceResponse.Result.builder()
                .network(CryptoNetwork.POLYGON)
                .token(CryptoToken.MATIC)
                .isAvailable(true)
                .commission(GetPaymentInvoiceServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPaymentFee)
                        .build())
                .build());

        //ETC
        servicesResultList.add(GetPaymentInvoiceServiceResponse.Result.builder()
                .network(CryptoNetwork.ETC)
                .token(CryptoToken.ETC)
                .isAvailable(true)
                .commission(GetPaymentInvoiceServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPaymentFee)
                        .build())
                .build());

        //AVAX
        servicesResultList.add(GetPaymentInvoiceServiceResponse.Result.builder()
                .network(CryptoNetwork.AVAX)
                .token(CryptoToken.AVAX)
                .isAvailable(true)
                .commission(GetPaymentInvoiceServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPaymentFee)
                        .build())
                .build());

        //BCH
        servicesResultList.add(GetPaymentInvoiceServiceResponse.Result.builder()
                .network(CryptoNetwork.BCH)
                .token(CryptoToken.BCH)
                .isAvailable(true)
                .commission(GetPaymentInvoiceServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPaymentFee)
                        .build())
                .build());

        //SOL
        servicesResultList.add(GetPaymentInvoiceServiceResponse.Result.builder()
                .network(CryptoNetwork.SOL)
                .token(CryptoToken.SOL)
                .isAvailable(true)
                .commission(GetPaymentInvoiceServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPaymentFee)
                        .build())
                .build());

        //LTC
        servicesResultList.add(GetPaymentInvoiceServiceResponse.Result.builder()
                .network(CryptoNetwork.LTC)
                .token(CryptoToken.LTC)
                .isAvailable(true)
                .commission(GetPaymentInvoiceServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPaymentFee)
                        .build())
                .build());

        return GetPaymentInvoiceServiceResponse.builder()
                .state(0)
                .result(servicesResultList)
                .build();
    }

    @Transactional
    @Override
    public CreateStaticWalletResponse createStaticWallet(String apiKey, CreateStaticWalletRequest createStaticWalletRequest) {
        if (staticWalletRepository.existsByOrderId(createStaticWalletRequest.getOrderId())) {
            throw new PaymentException("Payment with this order_id already exists");
        }

        Merchant principalMerchant = merchantRepository.findByApiKey(apiKey);

        String staticWalletId = nodeFactoryApi.createStaticWallet(NodeFactoryCreateStaticWalletRequest.builder()
                .apiKey(apiKey)
                .urlCallback(staticWalletsUrl)
                .build());

        String staticWalletAddress = nodeFactoryApi.getStaticWalletAddress(staticWalletId, createStaticWalletRequest.getNetwork());

        StaticWallet newStaticWallet = StaticWallet.builder()
                .status(StaticWalletStatus.ACTIVE)
                .walletId(staticWalletId)
                .uuid(existsOnStaticWalletUuid(UUID.randomUUID().toString()))
                .orderId(createStaticWalletRequest.getOrderId())
                .network(createStaticWalletRequest.getNetwork())
                .token(createStaticWalletRequest.getToken())
                .address(staticWalletAddress)
                .urlCallback(createStaticWalletRequest.getUrlCallback())
                .createdAt(System.currentTimeMillis())
                .merchant(principalMerchant)
                .build();

        newStaticWallet = staticWalletRepository.save(newStaticWallet);

        return CreateStaticWalletResponse.builder()
                .state(0)
                .result(staticWalletToStaticWalletDtoMapper.staticWalletToStaticWalletDto(newStaticWallet))
                .build();
    }

    @Transactional
    @Override
    public BlockStaticWalletResponse blockStaticWallet(String apiKey, BlockStaticWalletRequest blockStaticWalletRequest) {
        Merchant principalMerchant = merchantRepository.findByApiKey(apiKey);

        StaticWallet principalStaticWallet = null;

        if (staticWalletRepository.existsByUuidAndMerchant(blockStaticWalletRequest.getUuid(), principalMerchant)) {
            principalStaticWallet = staticWalletRepository.findByUuid(blockStaticWalletRequest.getUuid());
        } else if (staticWalletRepository.existsByOrderIdAndMerchant(blockStaticWalletRequest.getOrderId(), principalMerchant)) {
            principalStaticWallet = staticWalletRepository.findByOrderId(blockStaticWalletRequest.getOrderId());
        } else {
            throw new PaymentException("You do not have a static wallet with that uuid or order_id");
        }

        principalStaticWallet.setStatus(StaticWalletStatus.BLOCKED);

        principalStaticWallet = staticWalletRepository.save(principalStaticWallet);

        return BlockStaticWalletResponse.builder()
                .state(0)
                .result(BlockStaticWalletResponse.Result.builder()
                        .status(StaticWalletStatus.BLOCKED)
                        .uuid(principalStaticWallet.getUuid())
                        .build())
                .build();
    }

    @Override
    public ResponseEntity<byte[]> createStaticWalletQr(String apiKey, String uuid) {
        String address = staticWalletRepository.findAddressByUuid(uuid);

        if (address == null) {
            return ResponseEntity.notFound().build();
        }

        byte[] generatedAddress = null;

        try {
            generatedAddress = qrUtil.generateTextQRCode(address, 256, 256);
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
    public void handleStaticWalletWebHook(StaticWalletTransactionWebHookRequest staticWalletTransactionWebHookRequest) {
        StaticWallet staticWallet = staticWalletRepository.findByWalletId(staticWalletTransactionWebHookRequest.getWalletId());
        Merchant staticWalletMerchant = staticWallet.getMerchant();

        if (staticWallet.getStatus().equals(StaticWalletStatus.BLOCKED)) {
            return;
        }

        BigDecimal staticWalletTransactionAmountDecimal = new BigDecimal(staticWalletTransactionWebHookRequest.getAmount());

        Wallet wallet = walletRepository.findByNetworkAndTokenAndMerchant(staticWalletTransactionWebHookRequest.getNetwork(),
                staticWalletTransactionWebHookRequest.getToken(), staticWalletMerchant);

        BigInteger walletBalance = new BigInteger(wallet.getBalance());

        BigInteger staticWalletTransactionAmountWithComission =
                staticWalletTransactionAmountDecimal.subtract(staticWalletTransactionAmountDecimal.multiply(BigDecimal.valueOf(paymentFee)))
                        .toBigInteger();

        walletBalance = walletBalance.add(staticWalletTransactionAmountWithComission);

        wallet.setBalance(walletBalance.toString());

        walletRepository.save(wallet);

        if (staticWallet.getUrlCallback() != null) {
            PaymentWebHookRequest paymentWebHookRequest = PaymentWebHookRequest.builder()
                    .status(InvoiceStatus.SUCCESS)
                    .type(CryptoTransactionWebHookType.WALLET)
                    .paymentAmount(staticWalletTransactionWebHookRequest.getAmount().toString())
                    .merchantAmount(staticWalletTransactionAmountWithComission.toString())
                    .uuid(staticWallet.getUuid())
                    .orderId(staticWallet.getOrderId())
                    .network(staticWalletTransactionWebHookRequest.getNetwork())
                    .token(staticWalletTransactionWebHookRequest.getToken())
                    .txId(staticWalletTransactionWebHookRequest.getHash())
                    .isFinal(true)
                    .sign(sha256Util.encodeStringToSHA256(staticWallet.getUuid()
                    + "&" + staticWallet.getOrderId() + "&" + staticWalletTransactionWebHookRequest.getAmount()
                    + "&" + staticWalletTransactionWebHookRequest.getNetwork()
                    + "&" + staticWalletTransactionWebHookRequest.getToken()
                    + "&" + staticWalletMerchant.getApiKey()))
                    .build();

            cryptoTransactionWebHookApi.sendPayment(paymentWebHookRequest, staticWallet.getUrlCallback());
        }
    }

    private String existsOnStaticWalletUuid(String uuid) {
        if (staticWalletRepository.existsByUuid(uuid)) {
            return existsOnStaticWalletUuid(UUID.randomUUID().toString());
        } else {
            return uuid;
        }
    }

    private String existsOnInvoiceUuid(String uuid) {
        if (invoiceRepository.existsByUuid(uuid)) {
            return existsOnInvoiceUuid(UUID.randomUUID().toString());
        } else {
            return uuid;
        }
    }

    private BigInteger existsOnInvoicePendingPaymentAmount(BigInteger merchantAmount, CryptoToken token) {
        if (invoiceRepository.existsByPaymentAmountAndTokenAndStatus(merchantAmount.toString(), token, InvoiceStatus.PENDING)) {
            switch (token) {
                case ETH -> merchantAmount = merchantAmount.add(BigInteger.valueOf((long) 1e12));

                case ETC, AVAX -> merchantAmount = merchantAmount.add(BigInteger.valueOf((long) 1e14));

                case BNB -> merchantAmount = merchantAmount.add(BigInteger.valueOf((long) 1e13));

                case MATIC, DAI -> merchantAmount = merchantAmount.add(BigInteger.valueOf((long) 1e15));

                case BTC -> merchantAmount = merchantAmount.add(BigInteger.valueOf((long) 1e1));

                case LTC, BCH, USDT, USDC -> merchantAmount = merchantAmount.add(BigInteger.valueOf((long) 1e3));

                case TRX, SOL -> merchantAmount = merchantAmount.add(BigInteger.valueOf((long) 1e4));
            }

            return existsOnInvoicePendingPaymentAmount(merchantAmount, token);
        } else {
            return merchantAmount;
        }
    }
}