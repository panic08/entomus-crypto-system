package by.panic.entomus.service;

import by.panic.entomus.payload.payout.CreatePayoutRequest;
import by.panic.entomus.payload.payout.CreatePayoutResponse;
import by.panic.entomus.payload.payout.GetPayoutInfoResponse;

public interface PayoutService {
    CreatePayoutResponse create(String apiKey, CreatePayoutRequest createPayoutRequest);
    GetPayoutInfoResponse getInfo(String apiKey, String uuid, String orderId);
}
