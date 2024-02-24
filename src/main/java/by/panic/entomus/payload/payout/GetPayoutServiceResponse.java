package by.panic.entomus.payload.payout;

import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GetPayoutServiceResponse {
    private int state;
    private List<Result> result;

    @Getter
    @Setter
    @Builder
    @Schema(name = "GetPayoutServiceResponseResult")
    public static class Result {
        private CryptoNetwork network;
        private CryptoToken token;

        @JsonProperty("is_available")
        private boolean isAvailable;

        private Limit limit;
        private Commission commission;


        @Getter
        @Setter
        @Builder
        @Schema(name = "GetPayoutServiceResponseLimit")
        public static class Limit {
            @JsonProperty("min_amount")
            private String minAmount;

            @JsonProperty("min_converted_amount")
            private double minConvertedAmount;
        }

        @Getter
        @Setter
        @Builder
        @Schema(name = "GetPayoutServiceResponseLimit")
        public static class Commission {
            @JsonProperty("fee_amount")
            private double feeAmount;

            private double percent;
        }
    }
}
