package by.panic.entomus.service.implement;

import by.panic.entomus.entity.Merchant;
import by.panic.entomus.entity.Wallet;
import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import by.panic.entomus.entity.enums.WalletType;
import by.panic.entomus.mapper.WalletToWalletDtoMapperImpl;
import by.panic.entomus.payload.CreateMerchantResponse;
import by.panic.entomus.payload.merchant.GetMerchantBalanceResponse;
import by.panic.entomus.repository.MerchantRepository;
import by.panic.entomus.repository.WalletRepository;
import by.panic.entomus.service.MerchantService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MerchantServiceImpl implements MerchantService {
    private final MerchantRepository merchantRepository;
    private final WalletRepository walletRepository;
    private final WalletToWalletDtoMapperImpl walletToWalletDtoMapper;

    @Transactional
    @Override
    public CreateMerchantResponse create() {
        log.info("Starting creating a new Merchant in {}", MerchantServiceImpl.class);

        Merchant merchant = Merchant.builder()
                .apiKey(existsOnApiKey(UUID.randomUUID().toString()))
                .build();

        List<Wallet> wallets = new ArrayList<>();

        wallets.add(
                Wallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.AVAX)
                        .token(CryptoToken.AVAX)
                        .type(WalletType.BUSINESS)
                        .balance("0")
                        .build()
        );

        wallets.add(
                Wallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.BTC)
                        .token(CryptoToken.BTC)
                        .type(WalletType.BUSINESS)
                        .balance("0")
                        .build()
        );

        wallets.add(
                Wallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.LTC)
                        .token(CryptoToken.LTC)
                        .type(WalletType.BUSINESS)
                        .balance("0")
                        .build()
        );

        wallets.add(
                Wallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.BCH)
                        .token(CryptoToken.BCH)
                        .type(WalletType.BUSINESS)
                        .balance("0")
                        .build()
        );

        wallets.add(
                Wallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.BNB)
                        .token(CryptoToken.BNB)
                        .type(WalletType.BUSINESS)
                        .balance("0")
                        .build()
        );

        wallets.add(
                Wallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.ETC)
                        .token(CryptoToken.ETC)
                        .type(WalletType.BUSINESS)
                        .balance("0")
                        .build()
        );

        wallets.add(
                Wallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.ETH)
                        .token(CryptoToken.ETH)
                        .type(WalletType.BUSINESS)
                        .balance("0")
                        .build()
        );

        wallets.add(
                Wallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.POLYGON)
                        .token(CryptoToken.MATIC)
                        .type(WalletType.BUSINESS)
                        .balance("0")
                        .build()
        );

        wallets.add(
                Wallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.SOL)
                        .token(CryptoToken.SOL)
                        .type(WalletType.BUSINESS)
                        .balance("0")
                        .build()
        );

        wallets.add(
                Wallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.TRX)
                        .token(CryptoToken.TRX)
                        .type(WalletType.BUSINESS)
                        .balance("0")
                        .build()
        );

        wallets.add(
                Wallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.ETH)
                        .token(CryptoToken.USDT)
                        .type(WalletType.BUSINESS)
                        .balance("0")
                        .build()
        );

        wallets.add(
                Wallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.ETH)
                        .token(CryptoToken.USDC)
                        .type(WalletType.BUSINESS)
                        .balance("0")
                        .build()
        );

        wallets.add(
                Wallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.ETH)
                        .token(CryptoToken.DAI)
                        .type(WalletType.BUSINESS)
                        .balance("0")
                        .build()
        );

        wallets.add(
                Wallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.TRX)
                        .token(CryptoToken.USDT)
                        .type(WalletType.BUSINESS)
                        .balance("0")
                        .build()
        );

        wallets.add(
                Wallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.TRX)
                        .token(CryptoToken.USDC)
                        .type(WalletType.BUSINESS)
                        .balance("0")
                        .build()
        );

        wallets.add(
                Wallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.BNB)
                        .token(CryptoToken.USDT)
                        .type(WalletType.BUSINESS)
                        .balance("0")
                        .build()
        );

        wallets.add(
                Wallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.BNB)
                        .token(CryptoToken.USDC)
                        .type(WalletType.BUSINESS)
                        .balance("0")
                        .build()
        );

        wallets.add(
                Wallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.BNB)
                        .token(CryptoToken.DAI)
                        .type(WalletType.BUSINESS)
                        .balance("0")
                        .build()
        );

        merchant.setWallets(wallets);

        merchantRepository.save(merchant);

        log.info("Created new Merchant in {}", MerchantServiceImpl.class);

        return CreateMerchantResponse.builder()
                .state(0)
                .result(CreateMerchantResponse.Result.builder()
                        .apiKey(merchant.getApiKey())
                        .build())
                .build();
    }

    @Override
    public GetMerchantBalanceResponse getBalance(String apiKey) {
        return GetMerchantBalanceResponse.builder()
                .state(0)
                .result(GetMerchantBalanceResponse.Result.builder()
                        .balance(walletToWalletDtoMapper
                                .walletListToWalletDtoList(merchantRepository.findByApiKey(apiKey).getWallets()))
                        .build())
                .build();
    }

    private String existsOnApiKey(String apiKey) {
        if (merchantRepository.existsByApiKey(apiKey)) {
            return existsOnApiKey(UUID.randomUUID().toString());
        } else {
            return apiKey;
        }
    }

    private String existsOnWalletUUID(String uuid) {
        if (walletRepository.existsByUuid(uuid)) {
            return existsOnWalletUUID(UUID.randomUUID().toString());
        } else {
            return uuid;
        }
    }
}
