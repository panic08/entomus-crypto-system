package by.panic.entomus.payload.payment.invoice;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ResendPaymentInvoiceWebHookRequest {
    private String invoiceUuid;
    private String orderId;
}
