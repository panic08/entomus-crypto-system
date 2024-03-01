package by.panic.entomus.payload.payment;

import by.panic.entomus.dto.StaticWalletDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateStaticWalletResponse {
    private int state;
    private StaticWalletDto result;
}
