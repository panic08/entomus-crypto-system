package by.panic.entomus.api.payload.webhook;

import by.panic.entomus.api.payload.webhook.enums.WebHookType;
import by.panic.entomus.enums.CryptoNetwork;
import by.panic.entomus.enums.CryptoToken;
import by.panic.entomus.enums.InvoicePaymentCurrency;
import by.panic.entomus.enums.InvoiceStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WebHookRequest {
    private WebHookType type;
    private InvoiceStatus status;
    private String uuid;
    private String orderId;
    private double amount;
    private double paymentAmount;
    private InvoicePaymentCurrency currency;
    private String merchantAmount;
    private CryptoNetwork network;
    private CryptoToken token;
    private String additionalData;
    private String txId;
    private boolean isFinal;
    private String sign;
}
