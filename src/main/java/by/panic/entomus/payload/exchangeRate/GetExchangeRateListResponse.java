package by.panic.entomus.payload.exchangeRate;

import by.panic.entomus.entity.enums.CryptoToken;
import by.panic.entomus.payload.exchangeRate.enums.ExchangeRateCurrencyTo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GetExchangeRateListResponse {
    private int state;
    private List<Result> result;
    @Getter
    @Setter
    @Schema(name = "GetExchangeRateResponseResult")
    @NoArgsConstructor
    public static class Result {
        private CryptoToken from;
        private ExchangeRateCurrencyTo to;
        private double course;
    }
}
