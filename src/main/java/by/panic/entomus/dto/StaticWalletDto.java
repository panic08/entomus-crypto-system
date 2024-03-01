package by.panic.entomus.dto;

import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(name = "StaticWallet")
public class StaticWalletDto {
    private String uuid;

    private String orderId;

    private String address;

    private CryptoNetwork network;

    private CryptoToken token;

    private String urlCallback;

    private long createdAt;
}
