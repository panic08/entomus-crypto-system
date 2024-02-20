package by.panic.entomus.scheduler;

import by.panic.entomus.api.CoinGeckoApi;
import by.panic.entomus.api.payload.coingecko.CoinGeckoGetSimplePriceResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Data
@RequiredArgsConstructor
@Slf4j
public class CryptoCurrency {
    private final CoinGeckoApi coinGeckoApi;

    private Currency usd = new Currency();
    @Data
    public static class Currency {
        private double btc;
        private double eth;
        private double etc;
        private double trx;
        private double matic;
        private double avax;
        private double bnb;
        private double sol;
        private double ltc;
        private double bch;
        private double usdt;
        private double usdc;
        private double dai;
    }

    @Scheduled(fixedDelay = 30000)
    public void updateCryptoCurrency() {
        log.info("Starting updating cryptos prices on {}", CryptoCurrency.class);

        CoinGeckoGetSimplePriceResponse getCryptosPrice = coinGeckoApi.getCryptosPrice();

        getUsd().setBtc(getCryptosPrice.getBtc().getUsd());
        getUsd().setEth(getCryptosPrice.getEth().getUsd());
        getUsd().setEtc(getCryptosPrice.getEtc().getUsd());
        getUsd().setTrx(getCryptosPrice.getTrx().getUsd());
        getUsd().setMatic(getCryptosPrice.getMatic().getUsd());
        getUsd().setAvax(getCryptosPrice.getAvax().getUsd());
        getUsd().setBnb(getCryptosPrice.getBnb().getUsd());
        getUsd().setSol(getCryptosPrice.getSol().getUsd());
        getUsd().setLtc(getCryptosPrice.getLtc().getUsd());
        getUsd().setBch(getCryptosPrice.getBch().getUsd());
        getUsd().setUsdt(getCryptosPrice.getUsdt().getUsd());
        getUsd().setUsdc(getCryptosPrice.getUsdc().getUsd());
        getUsd().setDai(getCryptosPrice.getDai().getUsd());

        System.out.println(getUsd().getBtc());
    }
}
