package by.panic.entomus.api;

import by.panic.entomus.api.payload.coingecko.CoinGeckoGetSimplePriceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class CoinGeckoApi {
    private final RestTemplate restTemplate;
    private static final String COINGECKO_API_URL = "https://api.coingecko.com/api/v3";

    public CoinGeckoGetSimplePriceResponse getCryptosPrice() {
        ResponseEntity<CoinGeckoGetSimplePriceResponse> getSimplePriceResponseResponseEntity =
                restTemplate.getForEntity(COINGECKO_API_URL + "/simple/price?ids=bitcoin,ethereum,ethereum-classic,tron,matic-network,avalanche-2,binancecoin,solana,litecoin,bitcoin-cash,dai,tether,usd-coin&vs_currencies=usd",
                        CoinGeckoGetSimplePriceResponse.class);

        return getSimplePriceResponseResponseEntity.getBody();
    }
}
