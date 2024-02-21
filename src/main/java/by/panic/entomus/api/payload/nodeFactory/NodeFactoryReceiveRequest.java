package by.panic.entomus.api.payload.nodeFactory;

import by.panic.entomus.enums.CryptoNetwork;
import by.panic.entomus.enums.CryptoToken;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@Builder
public class NodeFactoryReceiveRequest {
    @JsonProperty("Blockchain")
    private CryptoNetwork network;

    @JsonProperty("Token")
    private CryptoToken token;

    @JsonProperty("Amount")
    private BigInteger amount;

    @JsonProperty("Timeout")
    private int timeout;
}
