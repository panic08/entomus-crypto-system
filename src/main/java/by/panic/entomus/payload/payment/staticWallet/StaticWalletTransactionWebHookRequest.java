package by.panic.entomus.payload.payment.staticWallet;

import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class StaticWalletTransactionWebHookRequest {
    @JsonProperty("walletID")
    private String walletId;

    private long block;

    private String hash;

    @JsonProperty("confirmations")
    private int confirmation;

    @JsonProperty("blockchain")
    private CryptoNetwork network;

    @JsonProperty("token")
    private CryptoToken token;

    private BigInteger amount;

    @JsonProperty("toaddress")
    private String toAddress;
}
