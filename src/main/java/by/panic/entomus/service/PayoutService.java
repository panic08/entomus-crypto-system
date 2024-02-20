package by.panic.entomus.service;

import by.panic.entomus.payload.payout.CreatePayoutRequest;
import by.panic.entomus.payload.payout.CreatePayoutResponse;

public interface PayoutService {
    CreatePayoutResponse create(String apiKey, CreatePayoutRequest createPayoutRequest);
}
