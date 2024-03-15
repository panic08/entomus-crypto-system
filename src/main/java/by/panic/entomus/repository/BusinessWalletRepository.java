package by.panic.entomus.repository;

import by.panic.entomus.entity.BusinessWallet;
import by.panic.entomus.entity.Merchant;
import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessWalletRepository extends JpaRepository<BusinessWallet, Long> {
    BusinessWallet findByNetworkAndTokenAndMerchant(CryptoNetwork network, CryptoToken token, Merchant merchant);
    boolean existsByUuid(String uuid);
}
