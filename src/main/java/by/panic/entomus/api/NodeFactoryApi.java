package by.panic.entomus.api;

import by.panic.entomus.api.payload.nodeFactory.NodeFactoryGetStatusResponse;
import by.panic.entomus.api.payload.nodeFactory.NodeFactoryReceiveRequest;
import by.panic.entomus.api.payload.nodeFactory.NodeFactoryReceiveResponse;
import by.panic.entomus.api.payload.nodeFactory.NodeFactorySendTokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownContentTypeException;

@Component
@RequiredArgsConstructor
public class NodeFactoryApi {
    private final RestTemplate restTemplate;

    @Value("${nodeFactoryApi.url}")
    private String URL;

    public NodeFactoryReceiveResponse receive(NodeFactoryReceiveRequest nodeFactoryReceiveRequest) {
        ResponseEntity<NodeFactoryReceiveResponse> nodeFactoryReceiveRequestResponseEntity = null;

        try {
            nodeFactoryReceiveRequestResponseEntity = restTemplate.postForEntity(URL + "/api/receive", nodeFactoryReceiveRequest,
                    NodeFactoryReceiveResponse.class);
        } catch (UnknownContentTypeException ignored) {
            return null;
        }

        return nodeFactoryReceiveRequestResponseEntity.getBody();
    }

    public NodeFactoryGetStatusResponse getStatus(long receiveRequestId) {
        ResponseEntity<NodeFactoryGetStatusResponse> nodeFactoryGetStatusResponseResponseEntity = null;

        try {
            nodeFactoryGetStatusResponseResponseEntity = restTemplate.getForEntity(URL + "/api/status?id=" + receiveRequestId,
                    NodeFactoryGetStatusResponse.class);
        } catch (UnknownContentTypeException ignored) {
            return null;
        }

        return nodeFactoryGetStatusResponseResponseEntity.getBody();
    }
}
