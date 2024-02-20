package by.panic.entomus.payload.payment;

import by.panic.entomus.enums.CryptoNetwork;
import by.panic.entomus.enums.CryptoToken;
import by.panic.entomus.enums.InvoicePaymentCurrency;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreatePaymentRequest {
    private double amount;

    private InvoicePaymentCurrency currency;

    private CryptoNetwork network;

    private CryptoToken token;

    private String orderId;

    private String urlReturn;

    private String urlSuccess;

    private String urlCallback;

    private int lifetime;

    private double discountPercent;

    private String additionalData;
}
