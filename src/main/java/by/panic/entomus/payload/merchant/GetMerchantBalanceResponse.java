package by.panic.entomus.payload.merchant;

import by.panic.entomus.dto.BusinessWalletDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GetMerchantBalanceResponse {
    private int state;
    private Result result;

    @Getter
    @Setter
    @Builder
    @Schema(name = "GetMerchantBalanceResponseResult")
    public static class Result {
        private List<BusinessWalletDto> balance;
    }
}
