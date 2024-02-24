package by.panic.entomus.service.implement;

import by.panic.entomus.entity.enums.CryptoToken;
import by.panic.entomus.payload.exchangeRate.GetExchangeRateListResponse;
import by.panic.entomus.payload.exchangeRate.enums.ExchangeRateCurrencyTo;
import by.panic.entomus.scheduler.CryptoCurrency;
import by.panic.entomus.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {
    private final CryptoCurrency cryptoCurrency;

    @Override
    public GetExchangeRateListResponse getList(String apiKey, CryptoToken from) {
        List<GetExchangeRateListResponse.Result> getExchangeRateListResultList = new ArrayList<>();
        GetExchangeRateListResponse.Result getExchangeRateListResultUsd = new GetExchangeRateListResponse.Result();

        switch (from) {
            case ETH -> {
                getExchangeRateListResultUsd.setFrom(from);
                getExchangeRateListResultUsd.setTo(ExchangeRateCurrencyTo.USD);
                getExchangeRateListResultUsd.setCourse(cryptoCurrency.getUsd().getEth());
            }
            case AVAX -> {
                getExchangeRateListResultUsd.setFrom(from);
                getExchangeRateListResultUsd.setTo(ExchangeRateCurrencyTo.USD);
                getExchangeRateListResultUsd.setCourse(cryptoCurrency.getUsd().getAvax());
            }
            case ETC -> {
                getExchangeRateListResultUsd.setFrom(from);
                getExchangeRateListResultUsd.setTo(ExchangeRateCurrencyTo.USD);
                getExchangeRateListResultUsd.setCourse(cryptoCurrency.getUsd().getEtc());
            }
            case BNB -> {
                getExchangeRateListResultUsd.setFrom(from);
                getExchangeRateListResultUsd.setTo(ExchangeRateCurrencyTo.USD);
                getExchangeRateListResultUsd.setCourse(cryptoCurrency.getUsd().getBnb());
            }
            case MATIC -> {
                getExchangeRateListResultUsd.setFrom(from);
                getExchangeRateListResultUsd.setTo(ExchangeRateCurrencyTo.USD);
                getExchangeRateListResultUsd.setCourse(cryptoCurrency.getUsd().getMatic());
            }
            case DAI -> {
                getExchangeRateListResultUsd.setFrom(from);
                getExchangeRateListResultUsd.setTo(ExchangeRateCurrencyTo.USD);
                getExchangeRateListResultUsd.setCourse(cryptoCurrency.getUsd().getDai());
            }
            case BTC -> {
                getExchangeRateListResultUsd.setFrom(from);
                getExchangeRateListResultUsd.setTo(ExchangeRateCurrencyTo.USD);
                getExchangeRateListResultUsd.setCourse(cryptoCurrency.getUsd().getBtc());
            }
            case BCH -> {
                getExchangeRateListResultUsd.setFrom(from);
                getExchangeRateListResultUsd.setTo(ExchangeRateCurrencyTo.USD);
                getExchangeRateListResultUsd.setCourse(cryptoCurrency.getUsd().getBch());
            }
            case LTC -> {
                getExchangeRateListResultUsd.setFrom(from);
                getExchangeRateListResultUsd.setTo(ExchangeRateCurrencyTo.USD);
                getExchangeRateListResultUsd.setCourse(cryptoCurrency.getUsd().getLtc());
            }
            case USDT -> {
                getExchangeRateListResultUsd.setFrom(from);
                getExchangeRateListResultUsd.setTo(ExchangeRateCurrencyTo.USD);
                getExchangeRateListResultUsd.setCourse(cryptoCurrency.getUsd().getUsdt());
            }
            case USDC -> {
                getExchangeRateListResultUsd.setFrom(from);
                getExchangeRateListResultUsd.setTo(ExchangeRateCurrencyTo.USD);
                getExchangeRateListResultUsd.setCourse(cryptoCurrency.getUsd().getUsdc());
            }
            case TRX -> {
                getExchangeRateListResultUsd.setFrom(from);
                getExchangeRateListResultUsd.setTo(ExchangeRateCurrencyTo.USD);
                getExchangeRateListResultUsd.setCourse(cryptoCurrency.getUsd().getTrx());
            }
            case SOL -> {
                getExchangeRateListResultUsd.setFrom(from);
                getExchangeRateListResultUsd.setTo(ExchangeRateCurrencyTo.USD);
                getExchangeRateListResultUsd.setCourse(cryptoCurrency.getUsd().getSol());
            }
        }

        getExchangeRateListResultList.add(getExchangeRateListResultUsd);

        return GetExchangeRateListResponse.builder()
                .state(0)
                .result(getExchangeRateListResultList)
                .build();
    }
}
