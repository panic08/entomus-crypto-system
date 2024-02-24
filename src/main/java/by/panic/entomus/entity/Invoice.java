package by.panic.entomus.entity;

import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import by.panic.entomus.entity.enums.InvoicePaymentCurrency;
import by.panic.entomus.entity.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "invoices_table")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private InvoiceStatus status;

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "order_id", unique = true, nullable = false)
    private String orderId;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "discount_percent", nullable = true)
    private Integer discountPercent;

    @Column(name = "discount", nullable = true)
    private Double discount;

    @Column(name = "payer_amount", nullable = false)
    private Double payerAmount;

    @Column(name = "payment_amount", nullable = false)
    private String paymentAmount;

    @Column(name = "currency", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private InvoicePaymentCurrency currency;

    @Column(name = "merchant_amount", nullable = false)
    private String merchantAmount;

    @Column(name = "network", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CryptoNetwork network;

    @Column(name = "token", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CryptoToken token;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "tx_id", nullable = true)
    private String txId;

    @Column(name = "additional_data", nullable = true)
    private String additionalData;

    @Column(name = "url_callback", nullable = true)
    private String urlCallback;

    @Column(name = "url_return", nullable = true)
    private String urlReturn;

    @Column(name = "url_success", nullable = true)
    private String urlSuccess;

    @Column(name = "final", nullable = false)
    private Boolean isFinal;

    @Column(name = "expired_at", nullable = false)
    private Long expiredAt;

    @Column(name = "updated_at", nullable = true)
    private Long updatedAt;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;
}
