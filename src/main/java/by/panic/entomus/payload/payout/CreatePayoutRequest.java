package by.panic.entomus.payload.payout;

import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreatePayoutRequest {
    @NotNull(message = "Amount not may be null")
    private String amount;

    @NotNull(message = "Token not may be null")
    private CryptoToken token;

    @NotNull(message = "Network not may be null")
    private CryptoNetwork network;

    @NotNull(message = "Address not may be null")
    private String address;

    @NotNull(message = "Order_id not may be null")
    private String orderId;

    private String urlCallback;

    @NotNull(message = "Subtract not may be null")
    @JsonProperty("is_subtract")
    private Boolean isSubtract;

}
