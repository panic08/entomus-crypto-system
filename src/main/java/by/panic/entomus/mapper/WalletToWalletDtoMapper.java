package by.panic.entomus.mapper;

import by.panic.entomus.dto.WalletDto;
import by.panic.entomus.entity.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WalletToWalletDtoMapper {
    @Mappings({
            @Mapping(target = "uuid", source = "uuid"),
            @Mapping(target = "network", source = "network"),
            @Mapping(target = "token", source = "token"),
            @Mapping(target = "balance", source = "balance"),
    })
    List<WalletDto> walletListToWalletDtoList(List<Wallet> walletList);
}
