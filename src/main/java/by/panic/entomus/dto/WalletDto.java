package by.panic.entomus.dto;

import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "Wallet")
public class WalletDto {
    private String uuid;
    private CryptoNetwork network;
    private CryptoToken token;
    private String balance;
}
