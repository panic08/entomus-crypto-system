package by.panic.entomus.api.payload.coingecko;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoinGeckoGetSimplePriceResponse {
    @JsonProperty("avalanche-2")
    private CryptoCurrency avax;

    @JsonProperty("binancecoin")
    private CryptoCurrency bnb;

    @JsonProperty("bitcoin")
    private CryptoCurrency btc;

    @JsonProperty("bitcoin-cash")
    private CryptoCurrency bch;

    @JsonProperty("dai")
    private CryptoCurrency dai;

    @JsonProperty("ethereum")
    private CryptoCurrency eth;

    @JsonProperty("ethereum-classic")
    private CryptoCurrency etc;

    @JsonProperty("litecoin")
    private CryptoCurrency ltc;

    @JsonProperty("matic-network")
    private CryptoCurrency matic;

    @JsonProperty("solana")
    private CryptoCurrency sol;

    @JsonProperty("tether")
    private CryptoCurrency usdt;

    @JsonProperty("tron")
    private CryptoCurrency trx;

    @JsonProperty("usd-coin")
    private CryptoCurrency usdc;

    @Getter
    @Setter
    public static class CryptoCurrency {
        private double usd;
    }
}
