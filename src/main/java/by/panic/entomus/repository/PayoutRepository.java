package by.panic.entomus.repository;

import by.panic.entomus.entity.Invoice;
import by.panic.entomus.entity.Payout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PayoutRepository extends JpaRepository<Payout, Long> {
    boolean existsByUuid(String uuid);
    boolean existsByOrderId(String orderId);
    Payout findByUuid(String uuid);
    Payout findByOrderId(String orderId);
}
