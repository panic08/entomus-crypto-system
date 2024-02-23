package by.panic.entomus.payload.payout;

import by.panic.entomus.dto.PayoutDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GetPayoutInfoResponse {
    private int state;
    private PayoutDto result;
}
