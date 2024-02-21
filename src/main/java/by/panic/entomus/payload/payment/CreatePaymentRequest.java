package by.panic.entomus.payload.payment;

import by.panic.entomus.enums.CryptoNetwork;
import by.panic.entomus.enums.CryptoToken;
import by.panic.entomus.enums.InvoicePaymentCurrency;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreatePaymentRequest {
    @NotNull(message = "Amount not may be null")
    private Double amount;

    @NotNull(message = "Currency not may be null")
    private InvoicePaymentCurrency currency;

    @NotNull(message = "Network not may be null")
    private CryptoNetwork network;

    @NotNull(message = "Token not may be null")
    private CryptoToken token;

    @NotNull(message = "Order_id not may be null")
    @Pattern(regexp = "[a-zA-Z0-9&\\-_+//]*", message = "Order_id must be a string consisting of alphabetic characters, numbers, underscores, and dashes.")
    @Size(min = 1, max = 128, message = "Order_id must contain from 1 to 128 characters")
    private String orderId;

    @Size(min = 6, max = 255, message = "Url_return must contain from 6 to 255 characters")
    private String urlReturn;

    @Size(min = 6, max = 255, message = "Url_success must contain from 6 to 255 characters")
    private String urlSuccess;

    @Size(min = 6, max = 255, message = "Url_callback must contain from 6 to 255 characters")
    private String urlCallback;

    @NotNull(message = "Lifetime not may be null")
    @Min(value = 300, message = "Lifetime can contain a number between 300 and 10800")
    @Max(value = 10800, message = "Lifetime can contain a number between 300 and 10800")
    private Integer lifetime;

    @Min(value = -9, message = "Discount_percent can contain a number between -99 and 100")
    @Max(value = 100, message = "Discount_percent can contain a number between -99 and 100")
    private Integer discountPercent;

    @Size(max = 255, message = "Additional_data must contain from 0 to 255 characters")
    private String additionalData;
}
