package by.panic.entomus.payload.payout;

import by.panic.entomus.dto.PayoutDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GetPayoutHistoryResponse {
    private int state;
    private Result result;

    @Getter
    @Setter
    @Builder
    @Schema(name = "GetPayoutHistoryResponseResult")
    public static class Result {
        private List<PayoutDto> items;
    }
}
