package by.panic.entomus.payload.payment.staticWallet;

import by.panic.entomus.entity.enums.StaticWalletStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BlockStaticWalletResponse {
    private int state;
    private Result result;

    @Getter
    @Setter
    @Builder
    @Schema(name = "BlockStaticWalletResponseResult")
    public static class Result {
        private String uuid;
        private StaticWalletStatus status;
    }
}
