package by.panic.entomus.payload.payment.invoice;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TestPaymentInvoiceWebHookResponse {
    private int state;
    private Object[] result;
}
