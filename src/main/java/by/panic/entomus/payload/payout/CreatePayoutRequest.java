package by.panic.entomus.payload.payout;

import by.panic.entomus.enums.CryptoNetwork;
import by.panic.entomus.enums.CryptoToken;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePayoutRequest {
    private String amount;

    private CryptoToken token;

    private CryptoNetwork network;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("url_callback")
    private String urlCallback;

}
