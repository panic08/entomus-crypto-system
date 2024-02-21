package by.panic.entomus.api.payload.nodeFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NodeFactoryReceiveResponse {
    @JsonProperty("ID")
    private long id;

    @JsonProperty("Address")
    private String address;
}
