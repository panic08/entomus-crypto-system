package by.panic.entomus.security;

import by.panic.entomus.exception.ApiKeyNotFound;
import by.panic.entomus.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MerchantSecurity {
    private final MerchantRepository merchantRepository;

    public void checkOnCorrectMerchant(String apiKey) {
        if (!merchantRepository.existsByApiKey(apiKey)) {
            throw new ApiKeyNotFound("Incorrect X-API-KEY");
        }
    }
}
