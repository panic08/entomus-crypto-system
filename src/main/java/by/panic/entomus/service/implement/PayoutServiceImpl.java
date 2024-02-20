package by.panic.entomus.service.implement;

import by.panic.entomus.exception.ApiKeyNotFound;
import by.panic.entomus.payload.payout.CreatePayoutRequest;
import by.panic.entomus.payload.payout.CreatePayoutResponse;
import by.panic.entomus.repository.MerchantRepository;
import by.panic.entomus.service.PayoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayoutServiceImpl implements PayoutService {
    private final MerchantRepository merchantRepository;

    @Override
    public CreatePayoutResponse create(String apiKey, CreatePayoutRequest createPayoutRequest) {
        if (!merchantRepository.existsByApiKey(apiKey)) {
            throw new ApiKeyNotFound("Incorrect X-API-KEY");
        }


        return null;
    }
}
