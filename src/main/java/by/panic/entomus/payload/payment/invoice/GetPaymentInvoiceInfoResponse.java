package by.panic.entomus.payload.payment.invoice;

import by.panic.entomus.dto.InvoiceDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetPaymentInvoiceInfoResponse {
    private int state;
    private InvoiceDto result;
}
