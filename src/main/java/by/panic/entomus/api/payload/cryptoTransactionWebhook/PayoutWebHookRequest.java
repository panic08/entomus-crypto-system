package by.panic.entomus.api.payload.cryptoTransactionWebhook;

import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import by.panic.entomus.entity.enums.PayoutStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PayoutWebHookRequest {
    private String uuid;
    private String orderId;
    private String amount;
    private String merchantAmount;

    @JsonProperty("is_final")
    private boolean isFinal;

    private PayoutStatus status;
    private String txId;
    private CryptoNetwork network;
    private CryptoToken token;
    private String sign;
}
