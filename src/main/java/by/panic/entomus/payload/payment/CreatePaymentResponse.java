package by.panic.entomus.payload.payment;

import by.panic.entomus.dto.InvoiceDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentResponse {
    private int state;
    private InvoiceDto result;
}
