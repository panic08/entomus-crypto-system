package by.panic.entomus.payload.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "CreatePaymentQrRequest")
public class CreatePaymentQrRequest {
    @JsonProperty("invoice_uuid")
    private String invoiceUuid;
}
