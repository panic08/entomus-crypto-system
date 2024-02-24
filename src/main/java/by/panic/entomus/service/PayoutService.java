package by.panic.entomus.service;

import by.panic.entomus.payload.payout.*;

public interface PayoutService {
    CreatePayoutResponse create(String apiKey, CreatePayoutRequest createPayoutRequest);
    GetPayoutInfoResponse getInfo(String apiKey, String uuid, String orderId);
    GetPayoutHistoryResponse getHistory(String apiKey, Long dateFrom, Long dateTo);
    GetPayoutServiceResponse getServices(String apiKey);
}
