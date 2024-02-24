package by.panic.entomus.service;

import by.panic.entomus.payload.CreateMerchantResponse;
import by.panic.entomus.payload.merchant.GetMerchantBalanceResponse;

public interface MerchantService {
    CreateMerchantResponse create();
    GetMerchantBalanceResponse getBalance(String apiKey);
}
