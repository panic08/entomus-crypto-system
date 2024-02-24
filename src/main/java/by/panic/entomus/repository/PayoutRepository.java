package by.panic.entomus.repository;

import by.panic.entomus.entity.Merchant;
import by.panic.entomus.entity.Payout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayoutRepository extends JpaRepository<Payout, Long> {
    boolean existsByUuid(String uuid);
    boolean existsByOrderId(String orderId);
    Payout findByUuid(String uuid);
    Payout findByOrderId(String orderId);

    @Query("SELECT p FROM payouts_table p WHERE p.merchant = :merchant AND p.createdAt > :dateFrom AND p.createdAt < :dateTo ORDER BY p.createdAt ASC")
    List<Payout> findAllByMerchantDateFilterOrderByCreatedAtAsc(@Param("merchant") Merchant merchant, @Param("dateFrom") long dateFrom, @Param("dateTo") long dateTo);
}
