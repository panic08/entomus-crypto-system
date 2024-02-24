package by.panic.entomus.payload.payment;

import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import by.panic.entomus.entity.enums.InvoiceStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TestPaymentInvoiceWebHookRequest {
        @NotNull(message = "Status not may be null")
    private InvoiceStatus status;

    @NotNull(message = "Uuid not may be null")
    private String uuid;

    @NotNull(message = "Order_id not may be null")
    @Pattern(regexp = "[a-zA-Z0-9&\\-_+//]*", message = "Order_id must be a string consisting of alphabetic characters, numbers, underscores, and dashes")
    @Size(min = 1, max = 128, message = "Order_id must contain from 1 to 128 characters")
    private String orderId;

    @NotNull(message = "Merchant_amount not may be null")
    private String merchantAmount;

    @NotNull(message = "Network not may be null")
    private CryptoNetwork network;

    @NotNull(message = "Token not may be null")
    private CryptoToken token;

    @Size(min = 6, max = 255, message = "Url_callback must contain from 6 to 255 characters")
    private String urlCallback;
}
