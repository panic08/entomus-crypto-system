package by.panic.entomus.payload.payment.staticWallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockStaticWalletRequest {
    private String uuid;

    @JsonProperty("order_id")
    private String orderId;
}
