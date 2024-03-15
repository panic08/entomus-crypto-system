package by.panic.entomus.entity;

import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "business_wallets_table")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusinessWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @Column(name = "network", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CryptoNetwork network;

    @Column(name = "token", nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CryptoToken token;

    @Column(name = "balance", nullable = false)
    private String balance;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;
}
