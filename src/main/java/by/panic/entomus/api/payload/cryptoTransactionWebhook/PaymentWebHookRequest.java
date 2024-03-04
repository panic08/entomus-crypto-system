package by.panic.entomus.api.payload.cryptoTransactionWebhook;

import by.panic.entomus.api.payload.cryptoTransactionWebhook.enums.CryptoTransactionWebHookType;
import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import by.panic.entomus.entity.enums.InvoicePaymentCurrency;
import by.panic.entomus.entity.enums.InvoiceStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentWebHookRequest {
    private CryptoTransactionWebHookType type;
    private InvoiceStatus status;
    private String uuid;
    private String orderId;
    private Double amount;
    private Double payerAmount;
    private String paymentAmount;
    private InvoicePaymentCurrency currency;
    private String merchantAmount;
    private CryptoNetwork network;
    private CryptoToken token;
    private String additionalData;
    private String txId;
    private boolean isFinal;
    private String sign;
}
