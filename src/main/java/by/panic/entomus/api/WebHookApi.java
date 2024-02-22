package by.panic.entomus.api;

import by.panic.entomus.api.payload.webhook.WebHookRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class WebHookApi {
    private final RestTemplate restTemplate;

    public void send(WebHookRequest webHookRequest, String URL) {
        ResponseEntity<Void> responseEntity = restTemplate.postForEntity(URL, webHookRequest, Void.class);
    }
}
