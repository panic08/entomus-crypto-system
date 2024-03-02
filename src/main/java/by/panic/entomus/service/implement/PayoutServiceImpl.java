package by.panic.entomus.service.implement;

import by.panic.entomus.api.CryptoTransactionWebHookApi;
import by.panic.entomus.api.NodeFactoryApi;
import by.panic.entomus.api.payload.cryptoTransactionWebhook.PayoutWebHookRequest;
import by.panic.entomus.api.payload.nodeFactory.NodeFactorySendRequest;
import by.panic.entomus.api.payload.nodeFactory.NodeFactorySendResponse;
import by.panic.entomus.entity.Merchant;
import by.panic.entomus.entity.Payout;
import by.panic.entomus.entity.Wallet;
import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import by.panic.entomus.entity.enums.PayoutStatus;
import by.panic.entomus.exception.PaymentException;
import by.panic.entomus.exception.PayoutException;
import by.panic.entomus.mapper.PayoutToPayoutDtoMapperImpl;
import by.panic.entomus.payload.payout.*;
import by.panic.entomus.property.PayoutLimitProperty;
import by.panic.entomus.repository.MerchantRepository;
import by.panic.entomus.repository.PayoutRepository;
import by.panic.entomus.repository.WalletRepository;
import by.panic.entomus.service.PayoutService;
import by.panic.entomus.util.SHA256Util;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayoutServiceImpl implements PayoutService {
    private final ExecutorService executorService;
    private final MerchantRepository merchantRepository;
    private final WalletRepository walletRepository;
    private final PayoutRepository payoutRepository;
    private final NodeFactoryApi nodeFactoryApi;
    private final CryptoTransactionWebHookApi cryptoTransactionWebHookApi;
    private final PayoutLimitProperty payoutLimitProperty;
    private final PayoutToPayoutDtoMapperImpl payoutToPayoutDtoMapper;
    private final SHA256Util sha256Util;

    @Value("${payouts.fee}")
    private Double payoutFee;

    @Transactional
    @Override
    public CreatePayoutResponse create(String apiKey, CreatePayoutRequest createPayoutRequest) {
        if (payoutRepository.existsByOrderId(createPayoutRequest.getOrderId())) {
            throw new PayoutException("Payout with this order_id already exists");
        }

        BigDecimal payoutAmountDecimal = new BigDecimal(createPayoutRequest.getAmount());

        switch (createPayoutRequest.getNetwork()) {
            case ETH -> {
                if (createPayoutRequest.getToken().equals(CryptoToken.ETH)
                        && payoutAmountDecimal.compareTo(new BigDecimal(payoutLimitProperty.getMinEth())) < 0) {
                    throw new PayoutException("The amount for create payout is too small");
                } else if (createPayoutRequest.getToken().equals(CryptoToken.USDT)
                        && payoutAmountDecimal.compareTo(new BigDecimal(payoutLimitProperty.getMinEthUsdt())) < 0) {
                    throw new PayoutException("The amount for create payout is too small");
                } else if (createPayoutRequest.getToken().equals(CryptoToken.USDC)
                        && payoutAmountDecimal.compareTo(new BigDecimal(payoutLimitProperty.getMinEthUsdc())) < 0) {
                    throw new PayoutException("The amount for create payout is too small");
                } else if (createPayoutRequest.getToken().equals(CryptoToken.DAI)
                        && payoutAmountDecimal.compareTo(new BigDecimal(payoutLimitProperty.getMinEthDai())) < 0) {
                    throw new PayoutException("The amount for create payout is too small");
                }
            }
            case BTC -> { if (payoutAmountDecimal.compareTo(new BigDecimal(payoutLimitProperty.getMinBtc())) < 0)
                throw new PayoutException("The amount for create payout is too small"); }
            case TRX -> {
                if (createPayoutRequest.getToken().equals(CryptoToken.TRX)
                        && payoutAmountDecimal.compareTo(new BigDecimal(payoutLimitProperty.getMinTrx())) < 0) {
                    throw new PayoutException("The amount for create payout is too small");
                } else if (createPayoutRequest.getToken().equals(CryptoToken.USDT)
                        && payoutAmountDecimal.compareTo(new BigDecimal(payoutLimitProperty.getMinTrxUsdt())) < 0) {
                    throw new PayoutException("The amount for create payout is too small");
                } else if (createPayoutRequest.getToken().equals(CryptoToken.USDC)
                        && payoutAmountDecimal.compareTo(new BigDecimal(payoutLimitProperty.getMinTrxUsdc())) < 0) {
                    throw new PayoutException("The amount for create payout is too small");
                }
            }
            case BNB -> {
                if (createPayoutRequest.getToken().equals(CryptoToken.BNB)
                        && payoutAmountDecimal.compareTo(new BigDecimal(payoutLimitProperty.getMinBnb())) < 0) {
                    throw new PayoutException("The amount for create payout is too small");
                } else if (createPayoutRequest.getToken().equals(CryptoToken.USDT)
                        && payoutAmountDecimal.compareTo(new BigDecimal(payoutLimitProperty.getMinBnbUsdt())) < 0) {
                    throw new PayoutException("The amount for create payout is too small");
                } else if (createPayoutRequest.getToken().equals(CryptoToken.USDC)
                        && payoutAmountDecimal.compareTo(new BigDecimal(payoutLimitProperty.getMinBnbUsdc())) < 0) {
                    throw new PayoutException("The amount for create payout is too small");
                } else if (createPayoutRequest.getToken().equals(CryptoToken.DAI)
                        && payoutAmountDecimal.compareTo(new BigDecimal(payoutLimitProperty.getMinBnbDai())) < 0) {
                    throw new PayoutException("The amount for create payout is too small");
                }
            }
            case POLYGON -> { if (payoutAmountDecimal.compareTo(new BigDecimal(payoutLimitProperty.getMinPolygon())) < 0)
                throw new PayoutException("The amount for create payout is too small"); }
            case ETC -> { if (payoutAmountDecimal.compareTo(new BigDecimal(payoutLimitProperty.getMinEtc())) < 0)
                throw new PayoutException("The amount for create payout is too small"); }
            case AVAX -> { if (payoutAmountDecimal.compareTo(new BigDecimal(payoutLimitProperty.getMinAvax())) < 0)
                throw new PayoutException("The amount for create payout is too small"); }
            case BCH -> { if (payoutAmountDecimal.compareTo(new BigDecimal(payoutLimitProperty.getMinBch())) < 0)
                throw new PayoutException("The amount for create payout is too small"); }
            case SOL -> { if (payoutAmountDecimal.compareTo(new BigDecimal(payoutLimitProperty.getMinSol())) < 0)
                throw new PayoutException("The amount for create payout is too small"); }
            case LTC -> { if (payoutAmountDecimal.compareTo(new BigDecimal(payoutLimitProperty.getMinLtc())) < 0)
                throw new PayoutException("The amount for create payout is too small"); }
        }

        Merchant principalMerchant = merchantRepository.findByApiKey(apiKey);

        Wallet payoutWallet = walletRepository.findByNetworkAndTokenAndMerchant(createPayoutRequest.getNetwork(), createPayoutRequest.getToken(),
                principalMerchant);

        BigDecimal payoutWalletBalanceDecimal = new BigDecimal(payoutWallet.getBalance());

        if (payoutWalletBalanceDecimal.compareTo(payoutAmountDecimal) < 0) {
            throw new PayoutException("Not enough funds");
        }

        if (createPayoutRequest.getIsSubtract() &&
                payoutWalletBalanceDecimal.compareTo(payoutAmountDecimal.add(payoutAmountDecimal.multiply(BigDecimal.valueOf(payoutFee)))) > 0) {
            payoutAmountDecimal = payoutAmountDecimal.add(payoutAmountDecimal.multiply(BigDecimal.valueOf(payoutFee)));
        } else if (!createPayoutRequest.getIsSubtract()){
            payoutAmountDecimal = payoutAmountDecimal.subtract(payoutAmountDecimal.multiply(BigDecimal.valueOf(payoutFee)));
        } else {
            throw new PayoutException("Not enough funds for Subtract = true");
        }

        BigInteger payoutWalletBalance = payoutWalletBalanceDecimal.toBigInteger();
        BigInteger payoutAmount = payoutAmountDecimal.toBigInteger();

        if (createPayoutRequest.getIsSubtract()) {
            payoutWalletBalance = payoutWalletBalance.subtract(payoutAmount);
        } else {
            payoutWalletBalance = payoutWalletBalance.subtract(new BigInteger(createPayoutRequest.getAmount()));
        }

        String payoutWalletBalanceString = payoutWalletBalance.toString();

        payoutWallet.setBalance(payoutWalletBalanceString);

        walletRepository.save(payoutWallet);

        Payout newPayout = Payout.builder()
                .status(PayoutStatus.SUCCESS)
                .uuid(existsOnUuid(UUID.randomUUID().toString()))
                .orderId(createPayoutRequest.getOrderId())
                .network(createPayoutRequest.getNetwork())
                .address(createPayoutRequest.getAddress())
                .token(createPayoutRequest.getToken())
                .amount(payoutAmount.toString())
                .urlCallback(createPayoutRequest.getUrlCallback())
                .balance(payoutWalletBalanceString)
                .isFinal(true)
                .createdAt(System.currentTimeMillis())
                .merchant(principalMerchant)
                .build();

        NodeFactorySendRequest nodeFactorySendRequest = NodeFactorySendRequest.builder()
                .address(createPayoutRequest.getAddress())
                .network(createPayoutRequest.getNetwork())
                .token(createPayoutRequest.getToken())
                .build();

        if (createPayoutRequest.getIsSubtract()) {
//            newPayout.setAmount(createPayoutRequest.getAmount());
            nodeFactorySendRequest.setAmount(new BigInteger(createPayoutRequest.getAmount()));
        } else {
//            newPayout.setAmount(payoutAmount.toString());
            nodeFactorySendRequest.setAmount(payoutAmount);
        }

//        NodeFactorySendResponse nodeFactorySendResponse = nodeFactoryApi.send(nodeFactorySendRequest);
//
//        newPayout.setTxId(nodeFactorySendResponse.getHash());
        newPayout.setTxId("fdsfsfsdfs");

        newPayout = payoutRepository.save(newPayout);

        Payout finalNewPayout = newPayout;

        if (createPayoutRequest.getUrlCallback() != null) {
            executorService.submit(() -> {
                PayoutWebHookRequest payoutWebHookRequest = PayoutWebHookRequest.builder()
                        .status(PayoutStatus.SUCCESS)
                        .uuid(finalNewPayout.getUuid())
                        .orderId(finalNewPayout.getOrderId())
                        .amount(createPayoutRequest.getAmount())
                        .merchantAmount(finalNewPayout.getAmount())
                        .isFinal(true)
                        .txId(finalNewPayout.getTxId())
                        .network(finalNewPayout.getNetwork())
                        .token(finalNewPayout.getToken())
                        .sign(sha256Util.encodeStringToSHA256(finalNewPayout.getUuid() + "&" + finalNewPayout.getOrderId()
                                + "&" + finalNewPayout.getAmount() + "&" + finalNewPayout.getNetwork()
                                + "&" + finalNewPayout.getToken() + "&" + apiKey))
                        .build();

                cryptoTransactionWebHookApi.sendPayout(payoutWebHookRequest, createPayoutRequest.getUrlCallback());
            });
        }

        return CreatePayoutResponse.builder()
                .state(0)
                .result(payoutToPayoutDtoMapper.payoutToPayoutDto(newPayout))
                .build();
    }

    @Override
    public GetPayoutInfoResponse getInfo(String apiKey, String uuid, String orderId) {
        Merchant principalMerchant = merchantRepository.findByApiKey(apiKey);

        Payout principalPayout;

        if (payoutRepository.existsByUuidAndMerchant(uuid, principalMerchant)) {
            principalPayout = payoutRepository.findByUuid(uuid);
        } else if (payoutRepository.existsByOrderIdAndMerchant(orderId, principalMerchant)) {
            principalPayout = payoutRepository.findByOrderId(orderId);
        } else {
            throw new PaymentException("You do not have a payout with that uuid or order_id");
        }

        return GetPayoutInfoResponse.builder()
                .state(0)
                .result(payoutToPayoutDtoMapper.payoutToPayoutDto(principalPayout))
                .build();
    }

    @Override
    public GetPayoutHistoryResponse getHistory(String apiKey, Long dateFrom, Long dateTo) {
        if (dateFrom == null) {
            dateFrom = 0L;
        }

        if (dateTo == null) {
            dateTo = 9999999999999L;
        }

        Merchant principalMerchant = merchantRepository.findByApiKey(apiKey);

        List<Payout> filteredPayoutList = payoutRepository.findAllByMerchantDateFilterOrderByCreatedAtAsc(principalMerchant,
                dateFrom, dateTo);

        return GetPayoutHistoryResponse.builder()
                .state(0)
                .result(GetPayoutHistoryResponse.Result.builder()
                        .items(payoutToPayoutDtoMapper.payoutListToPayoutDtoList(filteredPayoutList))
                        .build())
                .build();
    }

    @Override
    public GetPayoutServiceResponse getServices(String apiKey) {
        double percentPayoutFee = payoutFee * 100;
        List<GetPayoutServiceResponse.Result> servicesResultList = new ArrayList<>();

        //ETH
        servicesResultList.add(GetPayoutServiceResponse.Result.builder()
                .network(CryptoNetwork.ETH)
                .token(CryptoToken.ETH)
                .isAvailable(true)
                .limit(GetPayoutServiceResponse.Result.Limit.builder()
                        .minAmount(payoutLimitProperty.getMinEth())
                        .minConvertedAmount(new BigDecimal(payoutLimitProperty.getMinEth()).divide(BigDecimal.valueOf(1e18)).toString())
                        .build())
                .commission(GetPayoutServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPayoutFee)
                        .build())
                .build());

        servicesResultList.add(GetPayoutServiceResponse.Result.builder()
                .network(CryptoNetwork.ETH)
                .token(CryptoToken.USDT)
                .isAvailable(true)
                .limit(GetPayoutServiceResponse.Result.Limit.builder()
                        .minAmount(payoutLimitProperty.getMinEthUsdt())
                        .minConvertedAmount(new BigDecimal(payoutLimitProperty.getMinEthUsdt()).divide(BigDecimal.valueOf(1e6)).toString())
                        .build())
                .commission(GetPayoutServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPayoutFee)
                        .build())
                .build());

        servicesResultList.add(GetPayoutServiceResponse.Result.builder()
                .network(CryptoNetwork.ETH)
                .token(CryptoToken.USDC)
                .isAvailable(true)
                .limit(GetPayoutServiceResponse.Result.Limit.builder()
                        .minAmount(payoutLimitProperty.getMinEthUsdc())
                        .minConvertedAmount(new BigDecimal(payoutLimitProperty.getMinEthUsdc()).divide(BigDecimal.valueOf(1e6)).toString())
                        .build())
                .commission(GetPayoutServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPayoutFee)
                        .build())
                .build());

        servicesResultList.add(GetPayoutServiceResponse.Result.builder()
                .network(CryptoNetwork.ETH)
                .token(CryptoToken.DAI)
                .isAvailable(true)
                .limit(GetPayoutServiceResponse.Result.Limit.builder()
                        .minAmount(payoutLimitProperty.getMinEthDai())
                        .minConvertedAmount(new BigDecimal(payoutLimitProperty.getMinEthDai()).divide(BigDecimal.valueOf(1e18)).toString())
                        .build())
                .commission(GetPayoutServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPayoutFee)
                        .build())
                .build());

        //BTC
        servicesResultList.add(GetPayoutServiceResponse.Result.builder()
                .network(CryptoNetwork.BTC)
                .token(CryptoToken.BTC)
                .isAvailable(true)
                .limit(GetPayoutServiceResponse.Result.Limit.builder()
                        .minAmount(payoutLimitProperty.getMinBtc())
                        .minConvertedAmount(new BigDecimal(payoutLimitProperty.getMinBtc()).divide(BigDecimal.valueOf(1e8)).toString())
                        .build())
                .commission(GetPayoutServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPayoutFee)
                        .build())
                .build());

        //BNB
        servicesResultList.add(GetPayoutServiceResponse.Result.builder()
                .network(CryptoNetwork.BNB)
                .token(CryptoToken.BNB)
                .isAvailable(true)
                .limit(GetPayoutServiceResponse.Result.Limit.builder()
                        .minAmount(payoutLimitProperty.getMinBnb())
                        .minConvertedAmount(new BigDecimal(payoutLimitProperty.getMinBnb()).divide(BigDecimal.valueOf(1e18)).toString())
                        .build())
                .commission(GetPayoutServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPayoutFee)
                        .build())
                .build());

        servicesResultList.add(GetPayoutServiceResponse.Result.builder()
                .network(CryptoNetwork.BNB)
                .token(CryptoToken.USDT)
                .isAvailable(true)
                .limit(GetPayoutServiceResponse.Result.Limit.builder()
                        .minAmount(payoutLimitProperty.getMinBnbUsdt())
                        .minConvertedAmount(new BigDecimal(payoutLimitProperty.getMinBnbUsdt()).divide(BigDecimal.valueOf(1e6)).toString())
                        .build())
                .commission(GetPayoutServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPayoutFee)
                        .build())
                .build());

        servicesResultList.add(GetPayoutServiceResponse.Result.builder()
                .network(CryptoNetwork.BNB)
                .token(CryptoToken.USDC)
                .isAvailable(true)
                .limit(GetPayoutServiceResponse.Result.Limit.builder()
                        .minAmount(payoutLimitProperty.getMinBnbUsdc())
                        .minConvertedAmount(new BigDecimal(payoutLimitProperty.getMinBnbUsdc()).divide(BigDecimal.valueOf(1e6)).toString())
                        .build())
                .commission(GetPayoutServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPayoutFee)
                        .build())
                .build());

        servicesResultList.add(GetPayoutServiceResponse.Result.builder()
                .network(CryptoNetwork.BNB)
                .token(CryptoToken.DAI)
                .isAvailable(true)
                .limit(GetPayoutServiceResponse.Result.Limit.builder()
                        .minAmount(payoutLimitProperty.getMinBnbDai())
                        .minConvertedAmount(new BigDecimal(payoutLimitProperty.getMinBnbDai()).divide(BigDecimal.valueOf(1e18)).toString())
                        .build())
                .commission(GetPayoutServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPayoutFee)
                        .build())
                .build());

        //TRX
        servicesResultList.add(GetPayoutServiceResponse.Result.builder()
                .network(CryptoNetwork.TRX)
                .token(CryptoToken.TRX)
                .isAvailable(true)
                .limit(GetPayoutServiceResponse.Result.Limit.builder()
                        .minAmount(payoutLimitProperty.getMinTrx())
                        .minConvertedAmount(new BigDecimal(payoutLimitProperty.getMinTrx()).divide(BigDecimal.valueOf(1e6)).toString())
                        .build())
                .commission(GetPayoutServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPayoutFee)
                        .build())
                .build());

        servicesResultList.add(GetPayoutServiceResponse.Result.builder()
                .network(CryptoNetwork.TRX)
                .token(CryptoToken.USDT)
                .isAvailable(true)
                .limit(GetPayoutServiceResponse.Result.Limit.builder()
                        .minAmount(payoutLimitProperty.getMinTrxUsdt())
                        .minConvertedAmount(new BigDecimal(payoutLimitProperty.getMinTrxUsdt()).divide(BigDecimal.valueOf(1e6)).toString())
                        .build())
                .commission(GetPayoutServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPayoutFee)
                        .build())
                .build());

        servicesResultList.add(GetPayoutServiceResponse.Result.builder()
                .network(CryptoNetwork.TRX)
                .token(CryptoToken.USDC)
                .isAvailable(true)
                .limit(GetPayoutServiceResponse.Result.Limit.builder()
                        .minAmount(payoutLimitProperty.getMinTrxUsdc())
                        .minConvertedAmount(new BigDecimal(payoutLimitProperty.getMinTrxUsdc()).divide(BigDecimal.valueOf(1e6)).toString())
                        .build())
                .commission(GetPayoutServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPayoutFee)
                        .build())
                .build());

        //MATIC
        servicesResultList.add(GetPayoutServiceResponse.Result.builder()
                .network(CryptoNetwork.POLYGON)
                .token(CryptoToken.MATIC)
                .isAvailable(true)
                .limit(GetPayoutServiceResponse.Result.Limit.builder()
                        .minAmount(payoutLimitProperty.getMinPolygon())
                        .minConvertedAmount(new BigDecimal(payoutLimitProperty.getMinPolygon()).divide(BigDecimal.valueOf(1e18)).toString())
                        .build())
                .commission(GetPayoutServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPayoutFee)
                        .build())
                .build());

        //ETC
        servicesResultList.add(GetPayoutServiceResponse.Result.builder()
                .network(CryptoNetwork.ETC)
                .token(CryptoToken.ETC)
                .isAvailable(true)
                .limit(GetPayoutServiceResponse.Result.Limit.builder()
                        .minAmount(payoutLimitProperty.getMinEtc())
                        .minConvertedAmount(new BigDecimal(payoutLimitProperty.getMinEtc()).divide(BigDecimal.valueOf(1e18)).toString())
                        .build())
                .commission(GetPayoutServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPayoutFee)
                        .build())
                .build());

        //AVAX
        servicesResultList.add(GetPayoutServiceResponse.Result.builder()
                .network(CryptoNetwork.AVAX)
                .token(CryptoToken.AVAX)
                .isAvailable(true)
                .limit(GetPayoutServiceResponse.Result.Limit.builder()
                        .minAmount(payoutLimitProperty.getMinAvax())
                        .minConvertedAmount(new BigDecimal(payoutLimitProperty.getMinAvax()).divide(BigDecimal.valueOf(1e18)).toString())
                        .build())
                .commission(GetPayoutServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPayoutFee)
                        .build())
                .build());

        //BCH
        servicesResultList.add(GetPayoutServiceResponse.Result.builder()
                .network(CryptoNetwork.BCH)
                .token(CryptoToken.BCH)
                .isAvailable(true)
                .limit(GetPayoutServiceResponse.Result.Limit.builder()
                        .minAmount(payoutLimitProperty.getMinBch())
                        .minConvertedAmount(new BigDecimal(payoutLimitProperty.getMinBch()).divide(BigDecimal.valueOf(1e8)).toString())
                        .build())
                .commission(GetPayoutServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPayoutFee)
                        .build())
                .build());

        //SOL
        servicesResultList.add(GetPayoutServiceResponse.Result.builder()
                .network(CryptoNetwork.SOL)
                .token(CryptoToken.SOL)
                .isAvailable(true)
                .limit(GetPayoutServiceResponse.Result.Limit.builder()
                        .minAmount(payoutLimitProperty.getMinSol())
                        .minConvertedAmount(new BigDecimal(payoutLimitProperty.getMinSol()).divide(BigDecimal.valueOf(1e9)).toString())
                        .build())
                .commission(GetPayoutServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPayoutFee)
                        .build())
                .build());

        //LTC
        servicesResultList.add(GetPayoutServiceResponse.Result.builder()
                .network(CryptoNetwork.LTC)
                .token(CryptoToken.LTC)
                .isAvailable(true)
                .limit(GetPayoutServiceResponse.Result.Limit.builder()
                        .minAmount(payoutLimitProperty.getMinLtc())
                        .minConvertedAmount(new BigDecimal(payoutLimitProperty.getMinLtc()).divide(BigDecimal.valueOf(1e8)).toString())
                        .build())
                .commission(GetPayoutServiceResponse.Result.Commission.builder()
                        .feeAmount(0)
                        .percent(percentPayoutFee)
                        .build())
                .build());

        return GetPayoutServiceResponse.builder()
                .state(0)
                .result(servicesResultList)
                .build();
    }


    private String existsOnUuid(String uuid) {
        if (payoutRepository.existsByUuid(uuid)) {
            return existsOnUuid(UUID.randomUUID().toString());
        } else {
            return uuid;
        }
    }
}