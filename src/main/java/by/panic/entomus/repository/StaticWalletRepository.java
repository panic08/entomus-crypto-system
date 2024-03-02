package by.panic.entomus.repository;

import by.panic.entomus.entity.Merchant;
import by.panic.entomus.entity.StaticWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StaticWalletRepository extends JpaRepository<StaticWallet, Long> {
    @Query("SELECT sw.address FROM static_wallets_table sw WHERE sw.uuid = :uuid")
    String findAddressByUuid(@Param("uuid") String uuid);
    boolean existsByUuid(String uuid);
    boolean existsByOrderId(String orderId);
    boolean existsByUuidAndMerchant(String uuid, Merchant merchant);
    boolean existsByOrderIdAndMerchant(String orderId, Merchant merchant);
    StaticWallet findByUuid(String uuid);
    StaticWallet findByOrderId(String orderId);
}
