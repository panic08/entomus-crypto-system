package by.panic.entomus.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentQrRequest {
    @JsonProperty("invoice_uuid")
    private String invoiceUuid;
}
