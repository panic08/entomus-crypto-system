package by.panic.entomus.service;

import by.panic.entomus.entity.enums.CryptoToken;
import by.panic.entomus.payload.exchangeRate.GetExchangeRateListResponse;

public interface ExchangeRateService {
    GetExchangeRateListResponse getList(String apiKey, CryptoToken from);
}
