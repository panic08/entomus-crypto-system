package by.panic.entomus.dto;

import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import by.panic.entomus.entity.enums.InvoicePaymentCurrency;
import by.panic.entomus.entity.enums.InvoiceStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(name = "Invoice")
public class InvoiceDto {
    private InvoiceStatus status;

    private String uuid;

    private String orderId;

    private double amount;

    private String paymentAmount;

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

    @JsonProperty("is_final")
    private boolean isFinal;

    private long expiredAt;

    private Long updatedAt;

    private long createdAt;
}
