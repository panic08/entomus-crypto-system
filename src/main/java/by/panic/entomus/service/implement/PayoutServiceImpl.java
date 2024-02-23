package by.panic.entomus.service.implement;

import by.panic.entomus.api.NodeFactoryApi;
import by.panic.entomus.api.payload.nodeFactory.NodeFactorySendRequest;
import by.panic.entomus.api.payload.nodeFactory.NodeFactorySendResponse;
import by.panic.entomus.entity.Merchant;
import by.panic.entomus.entity.Payout;
import by.panic.entomus.entity.Wallet;
import by.panic.entomus.entity.enums.PayoutStatus;
import by.panic.entomus.exception.ApiKeyNotFound;
import by.panic.entomus.exception.PaymentException;
import by.panic.entomus.exception.PayoutException;
import by.panic.entomus.mapper.PayoutToPayoutDtoMapperImpl;
import by.panic.entomus.payload.payment.GetPaymentInfoResponse;
import by.panic.entomus.payload.payout.CreatePayoutRequest;
import by.panic.entomus.payload.payout.CreatePayoutResponse;
import by.panic.entomus.payload.payout.GetPayoutInfoResponse;
import by.panic.entomus.repository.MerchantRepository;
import by.panic.entomus.repository.PayoutRepository;
import by.panic.entomus.repository.WalletRepository;
import by.panic.entomus.security.MerchantSecurity;
import by.panic.entomus.service.PayoutService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayoutServiceImpl implements PayoutService {
    private final MerchantSecurity merchantSecurity;
    private final MerchantRepository merchantRepository;
    private final WalletRepository walletRepository;
    private final PayoutRepository payoutRepository;
    private final NodeFactoryApi nodeFactoryApi;
    private final PayoutToPayoutDtoMapperImpl payoutToPayoutDtoMapper;

    @Value("${payouts.fee}")
    private Double payoutFee;

    @Transactional
    @Override
    public CreatePayoutResponse create(String apiKey, CreatePayoutRequest createPayoutRequest) {
        merchantSecurity.checkOnCorrectMerchant(apiKey);

        if (payoutRepository.existsByOrderId(createPayoutRequest.getOrderId())) {
            throw new PayoutException("Payout with this order_id already exists");
        }

        BigDecimal payoutAmountDecimal = new BigDecimal(createPayoutRequest.getAmount());

        switch (createPayoutRequest.getNetwork()) {
            case ETH -> {  }
            case BTC -> {  }
            case TRX -> {  }
            case BNB -> {  }
            case POLYGON -> {  }
            case ETC -> {  }
            case AVAX -> {  }
            case BCH -> {  }
            case SOL -> {  }
            case LTC -> {  }
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
            newPayout.setAmount(createPayoutRequest.getAmount());
            nodeFactorySendRequest.setAmount(new BigInteger(createPayoutRequest.getAmount()));
        } else {
            newPayout.setAmount(payoutAmount.toString());
            nodeFactorySendRequest.setAmount(payoutAmount);
        }

        NodeFactorySendResponse nodeFactorySendResponse = nodeFactoryApi.send(nodeFactorySendRequest);

        newPayout.setTxId(nodeFactorySendResponse.getHash());

        newPayout = payoutRepository.save(newPayout);

        return CreatePayoutResponse.builder()
                .state(0)
                .result(payoutToPayoutDtoMapper.payoutToPayoutDto(newPayout))
                .build();
    }

    @Override
    public GetPayoutInfoResponse getInfo(String apiKey, String uuid, String orderId) {
        merchantSecurity.checkOnCorrectMerchant(apiKey);

        if (uuid != null) {
            return GetPayoutInfoResponse.builder()
                    .state(0)
                    .result(payoutToPayoutDtoMapper.payoutToPayoutDto(payoutRepository.findByUuid(uuid)))
                    .build();
        } else if (orderId != null) {
            return GetPayoutInfoResponse.builder()
                    .state(0)
                    .result(payoutToPayoutDtoMapper.payoutToPayoutDto(payoutRepository.findByOrderId(orderId)))
                    .build();
        }

        throw new PaymentException("Provide uuid or orderId");
    }

    private String existsOnUuid(String uuid) {
        if (payoutRepository.existsByUuid(uuid)) {
            return existsOnUuid(UUID.randomUUID().toString());
        } else {
            return uuid;
        }
    }
}
