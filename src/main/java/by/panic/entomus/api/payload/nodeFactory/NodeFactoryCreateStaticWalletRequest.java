package by.panic.entomus.api.payload.nodeFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NodeFactoryCreateStaticWalletRequest {
    @JsonProperty("apiKey")
    private String apiKey;
}
