package by.panic.entomus.payload.payment;

import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GetPaymentInvoiceServiceResponse {
    private int state;
    private List<Result> result;

    @Getter
    @Setter
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Schema(name = "GetPaymentServiceResponseResult")
    public static class Result {
        private CryptoNetwork network;
        private CryptoToken token;
        private boolean isAvailable;
        private Commission commission;

        @Getter
        @Setter
        @Builder
        @Schema(name = "GetPaymentServiceResponseCommission")
        public static class Commission {
            @JsonProperty("fee_amount")
            private double feeAmount;
            private double percent;
        }
    }
}
