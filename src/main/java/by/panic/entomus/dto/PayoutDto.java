package by.panic.entomus.dto;

import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import by.panic.entomus.entity.enums.PayoutStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(name = "Payout")
public class PayoutDto {
    private PayoutStatus status;

    private String uuid;

    private String orderId;

    private String amount;

    private CryptoNetwork network;

    private CryptoToken token;

    private String address;

    private String txId;

    private String balance;

    @JsonProperty("is_final")
    private boolean isFinal;

    private String urlCallback;

    private Long createdAt;
}
