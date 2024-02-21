package by.panic.entomus.api.payload.nodeFactory.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum NodeFactoryGetStatusStatus {
    @JsonProperty("pending")
    PENDING,
    @JsonProperty("found")
    FOUND,
    @JsonProperty("success")
    SUCCESS
}
