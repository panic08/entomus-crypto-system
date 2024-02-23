package by.panic.entomus.payload.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TestWebHookResponse {
    private int state;
    private Object[] result;
}
