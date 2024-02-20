package by.panic.entomus.api;

import by.panic.entomus.api.payload.nodeFactory.NodeFactorySendTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class NodeFactoryApi {
    private final RestTemplate restTemplate;

    @Value("${nodeFactoryApi.url}")
    private String URL;

    public void sendPayout(NodeFactorySendTokenRequest nodeFactorySendTokenRequest) {
    }
}
