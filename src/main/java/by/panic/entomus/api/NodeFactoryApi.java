package by.panic.entomus.api;

import by.panic.entomus.api.payload.nodeFactory.*;
import by.panic.entomus.exception.NodeFactoryException;
import by.panic.entomus.exception.PayoutException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownContentTypeException;

@Component
@RequiredArgsConstructor
@Slf4j
public class NodeFactoryApi {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

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

    public NodeFactorySendResponse send(NodeFactorySendRequest nodeFactorySendRequest) {
        ResponseEntity<String> nodeFactorySendResponseResponseEntity = null;

        nodeFactorySendResponseResponseEntity = restTemplate.postForEntity(URL + "/api/send", nodeFactorySendRequest,
                String.class);

        if (nodeFactorySendResponseResponseEntity.getBody().equals("failed to execute send: failed to send trx: failed to get dest: checksum error")) {
            throw new NodeFactoryException("You have entered an incorrect network address");
        } else if (nodeFactorySendResponseResponseEntity.getBody().equals("failed to create new transactor: transactor not found")) {
            throw new NodeFactoryException("You have entered an invalid Network-Token pair");
        }

        NodeFactorySendResponse nodeFactorySendResponse = null;

        try {
            nodeFactorySendResponse = objectMapper.readValue(nodeFactorySendResponseResponseEntity.getBody(),
                    NodeFactorySendResponse.class);
        } catch (Exception e) {
            log.warn(e.getMessage());

            throw new NodeFactoryException("NodeFactory service system error");
        }

        return nodeFactorySendResponse;
    }
}
