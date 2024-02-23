package by.panic.entomus.payload.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResendWebHookResponse {
    private int state;
    private Object[] result;
}
