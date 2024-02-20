package by.panic.entomus.payload.payout;

import by.panic.entomus.enums.CryptoNetwork;
import by.panic.entomus.enums.CryptoToken;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreatePayoutResponse {
    private int state;

    @Getter
    @Setter
    public static class CreatePayloadResponseResult {
        private String uuid;

        private String amount;

        private CryptoToken token;

        private CryptoNetwork network;

        private String address;

        @JsonProperty("tx_id")
        private String txId;

        private Long status;

        @JsonProperty("is_final")
        private Boolean isFinal;
    }
}
