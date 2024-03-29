package by.panic.entomus.entity;

import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import by.panic.entomus.entity.enums.StaticWalletStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "static_wallets_table")
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaticWallet extends Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wallet_id", unique = true, nullable = false)
    private String walletId;

    @Column(name = "status", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private StaticWalletStatus status;

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "order_id", unique = true, nullable = false)
    private String orderId;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "network", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CryptoNetwork network;

    @Column(name = "token", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CryptoToken token;

    @Column(name = "url_callback", nullable = true)
    private String urlCallback;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;
}
