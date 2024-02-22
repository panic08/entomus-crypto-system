package by.panic.entomus.repository;

import by.panic.entomus.entity.Invoice;
import by.panic.entomus.enums.CryptoToken;
import by.panic.entomus.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    boolean existsByOrderId(String orderId);
    boolean existsByUuid(String uuid);
    boolean existsByMerchantAmountAndTokenAndStatus(String merchantAmount, CryptoToken token, InvoiceStatus status);
    @Query("SELECT i.address FROM invoices_table i WHERE i.uuid = :uuid")
    String findAddressByUuid(@Param("uuid") String uuid);
    Invoice findByUuid(String uuid);
    Invoice findByOrderId(String orderId);
}
