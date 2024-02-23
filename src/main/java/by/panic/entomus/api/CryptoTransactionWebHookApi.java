package by.panic.entomus.api;

import by.panic.entomus.api.payload.cryptoTransactionWebhook.PaymentWebHookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class CryptoTransactionWebHookApi {
    private final RestTemplate restTemplate;

    public void sendPayment(PaymentWebHookRequest webHookRequest, String URL) {
        restTemplate.postForEntity(URL, webHookRequest, Void.class);
    }
}
