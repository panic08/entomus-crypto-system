package by.panic.entomus.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ExceptionHandler {
    private int state;
    @JsonProperty("exception_message")
    private String exceptionMessage;
}
