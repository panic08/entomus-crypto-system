package by.panic.entomus.repository;

import by.panic.entomus.entity.Merchant;
import by.panic.entomus.entity.Wallet;
import by.panic.entomus.enums.CryptoNetwork;
import by.panic.entomus.enums.CryptoToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Wallet findByNetworkAndTokenAndMerchant(CryptoNetwork network, CryptoToken token, Merchant merchant);
    boolean existsByUuid(String uuid);
}
