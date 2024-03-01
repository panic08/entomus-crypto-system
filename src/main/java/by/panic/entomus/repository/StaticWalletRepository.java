package by.panic.entomus.repository;

import by.panic.entomus.entity.StaticWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaticWalletRepository extends JpaRepository<StaticWallet, Long> {
    boolean existsByUuid(String uuid);
    boolean existsByOrderId(String orderId);
}
