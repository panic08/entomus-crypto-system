package by.panic.entomus.entity;

import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import by.panic.entomus.entity.enums.StaticWalletStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "static_wallets_table")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StaticWallet {
    @Id
    private Long id;

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

    @Column(name = "url_callback", nullable = false)
    private String urlCallback;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;
}
