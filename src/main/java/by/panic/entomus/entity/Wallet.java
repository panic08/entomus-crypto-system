package by.panic.entomus.entity;

import by.panic.entomus.enums.CryptoNetwork;
import by.panic.entomus.enums.CryptoToken;
import by.panic.entomus.enums.WalletType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "wallets_table")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "type", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private WalletType type;

    @Column(name = "network", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CryptoNetwork network;

    @Column(name = "token", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CryptoToken token;

    @Column(name = "address", nullable = true)
    private String address;

    @Column(name = "balance", nullable = true)
    private Long balance;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;
}
