package by.panic.entomus.dto;

import by.panic.entomus.enums.CryptoNetwork;
import by.panic.entomus.enums.CryptoToken;
import by.panic.entomus.enums.InvoicePaymentCurrency;
import by.panic.entomus.enums.InvoiceStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InvoiceDto {
    private InvoiceStatus status;

    private String uuid;

    private String orderId;

    private double amount;

    private double payerAmount;

    private double discount;

    private int discountPercent;

    private InvoicePaymentCurrency currency;

    private String merchantAmount;

    private CryptoNetwork network;

    private CryptoToken token;

    private String address;

    private String txId;

    private String additionalData;

    private String urlReturn;

    private String urlSuccess;

    private boolean isFinal;

    private long expiredAt;

    private Long updatedAt;

    private long createdAt;
}
