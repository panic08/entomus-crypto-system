package by.panic.entomus.repository;

import by.panic.entomus.entity.Invoice;
import by.panic.entomus.entity.Merchant;
import by.panic.entomus.entity.enums.CryptoToken;
import by.panic.entomus.entity.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    boolean existsByOrderId(String orderId);
    boolean existsByUuid(String uuid);
    boolean existsByOrderIdAndMerchant(String orderId, Merchant merchant);
    boolean existsByUuidAndMerchant(String uuid, Merchant merchant);
    boolean existsByPaymentAmountAndTokenAndStatus(String paymentAmount, CryptoToken token, InvoiceStatus status);
    @Query("SELECT i.address FROM invoices_table i WHERE i.uuid = :uuid")
    String findAddressByUuid(@Param("uuid") String uuid);
    Invoice findByUuid(String uuid);
    Invoice findByOrderId(String orderId);
    @Query("SELECT i FROM invoices_table i WHERE i.merchant = :merchant AND i.createdAt > :dateFrom AND i.createdAt < :dateTo ORDER BY i.createdAt ASC")
    List<Invoice> findAllByMerchantDateFilterOrderByCreatedAtAsc(@Param("merchant") Merchant merchant, @Param("dateFrom") long dateFrom, @Param("dateTo") long dateTo);
}
