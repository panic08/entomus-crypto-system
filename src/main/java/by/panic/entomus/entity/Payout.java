package by.panic.entomus.entity;

import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import by.panic.entomus.entity.enums.PayoutStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "payouts_table")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "status", nullable = false)
    private PayoutStatus status;

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "order_id", unique = true, nullable = false)
    private String orderId;

    @Column(name = "amount", nullable = false)
    private String amount;

    @Column(name = "network", nullable = false)
    private CryptoNetwork network;

    @Column(name = "token", nullable = false)
    private CryptoToken token;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "tx_id", nullable = true)
    private String txId;

    @Column(name = "balance", nullable = false)
    private String balance;

    @Column(name = "final", nullable = false)
    private Boolean isFinal;

    @Column(name = "url_callback", nullable = true)
    private String urlCallback;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;
}
