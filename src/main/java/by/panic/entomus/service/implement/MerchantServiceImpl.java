package by.panic.entomus.service.implement;

import by.panic.entomus.entity.BusinessWallet;
import by.panic.entomus.entity.Merchant;
import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import by.panic.entomus.mapper.BusinessWalletToBusinessWalletDtoMapperImpl;
import by.panic.entomus.payload.CreateMerchantResponse;
import by.panic.entomus.payload.merchant.GetMerchantBalanceResponse;
import by.panic.entomus.repository.MerchantRepository;
import by.panic.entomus.repository.BusinessWalletRepository;
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
    private final BusinessWalletRepository businessWalletRepository;
    private final BusinessWalletToBusinessWalletDtoMapperImpl businessWalletToBusinessWalletDtoMapper;

    /**
     * This method is needed to create merchant in the system
     */

    @Transactional
    @Override
    public CreateMerchantResponse create() {
        log.info("Starting creating a new Merchant in {}", MerchantServiceImpl.class);

        Merchant merchant = Merchant.builder()
                .apiKey(existsOnApiKey(UUID.randomUUID().toString()))
                .build();

        List<BusinessWallet> businessWallets = new ArrayList<>();

        businessWallets.add(
                BusinessWallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.AVAX)
                        .token(CryptoToken.AVAX)
                        .balance("0")
                        .build()
        );

        businessWallets.add(
                BusinessWallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.BTC)
                        .token(CryptoToken.BTC)
                        .balance("0")
                        .build()
        );

        businessWallets.add(
                BusinessWallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.LTC)
                        .token(CryptoToken.LTC)
                        .balance("0")
                        .build()
        );

        businessWallets.add(
                BusinessWallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.BCH)
                        .token(CryptoToken.BCH)
                        .balance("0")
                        .build()
        );

        businessWallets.add(
                BusinessWallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.BNB)
                        .token(CryptoToken.BNB)
                        .balance("0")
                        .build()
        );

        businessWallets.add(
                BusinessWallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.ETC)
                        .token(CryptoToken.ETC)
                        .balance("0")
                        .build()
        );

        businessWallets.add(
                BusinessWallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.ETH)
                        .token(CryptoToken.ETH)
                        .balance("0")
                        .build()
        );

        businessWallets.add(
                BusinessWallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.POLYGON)
                        .token(CryptoToken.MATIC)
                        .balance("0")
                        .build()
        );

        businessWallets.add(
                BusinessWallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.SOL)
                        .token(CryptoToken.SOL)
                        .balance("0")
                        .build()
        );

        businessWallets.add(
                BusinessWallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.TRX)
                        .token(CryptoToken.TRX)
                        .balance("0")
                        .build()
        );

        businessWallets.add(
                BusinessWallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.ETH)
                        .token(CryptoToken.USDT)
                        .balance("0")
                        .build()
        );

        businessWallets.add(
                BusinessWallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.ETH)
                        .token(CryptoToken.USDC)
                        .balance("0")
                        .build()
        );

        businessWallets.add(
                BusinessWallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.ETH)
                        .token(CryptoToken.DAI)
                        .balance("0")
                        .build()
        );

        businessWallets.add(
                BusinessWallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.TRX)
                        .token(CryptoToken.USDT)
                        .balance("0")
                        .build()
        );

        businessWallets.add(
                BusinessWallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.TRX)
                        .token(CryptoToken.USDC)
                        .balance("0")
                        .build()
        );

        businessWallets.add(
                BusinessWallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.BNB)
                        .token(CryptoToken.USDT)
                        .balance("0")
                        .build()
        );

        businessWallets.add(
                BusinessWallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.BNB)
                        .token(CryptoToken.USDC)
                        .balance("0")
                        .build()
        );

        businessWallets.add(
                BusinessWallet.builder()
                        .merchant(merchant)
                        .uuid(existsOnWalletUUID(UUID.randomUUID().toString()))
                        .network(CryptoNetwork.BNB)
                        .token(CryptoToken.DAI)
                        .balance("0")
                        .build()
        );

        merchant.setBusinessWallets(businessWallets);

        merchantRepository.save(merchant);

        log.info("Created new Merchant in {}", MerchantServiceImpl.class);

        return CreateMerchantResponse.builder()
                .state(0)
                .result(CreateMerchantResponse.Result.builder()
                        .apiKey(merchant.getApiKey())
                        .build())
                .build();
    }

    /**
     * This method is needed to get merchant balance in the system
     */

    @Override
    public GetMerchantBalanceResponse getBalance(String apiKey) {
        return GetMerchantBalanceResponse.builder()
                .state(0)
                .result(GetMerchantBalanceResponse.Result.builder()
                        .balance(businessWalletToBusinessWalletDtoMapper
                                .businessWalletListToBusinessWalletDtoList(merchantRepository.findByApiKey(apiKey).getBusinessWallets()))
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
        if (businessWalletRepository.existsByUuid(uuid)) {
            return existsOnWalletUUID(UUID.randomUUID().toString());
        } else {
            return uuid;
        }
    }
}
