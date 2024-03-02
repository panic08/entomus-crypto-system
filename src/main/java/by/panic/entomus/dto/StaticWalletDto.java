package by.panic.entomus.dto;

import by.panic.entomus.entity.enums.CryptoNetwork;
import by.panic.entomus.entity.enums.CryptoToken;
import by.panic.entomus.entity.enums.StaticWalletStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(name = "StaticWallet")
public class StaticWalletDto {
    private StaticWalletStatus status;

    private String uuid;

    private String orderId;

    private String address;

    private CryptoNetwork network;

    private CryptoToken token;

    private String urlCallback;

    private long createdAt;
}
