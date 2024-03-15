package by.panic.entomus.mapper;

import by.panic.entomus.dto.BusinessWalletDto;
import by.panic.entomus.entity.BusinessWallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BusinessWalletToBusinessWalletDtoMapper {
    @Mappings({
            @Mapping(target = "uuid", source = "uuid"),
            @Mapping(target = "network", source = "network"),
            @Mapping(target = "token", source = "token"),
            @Mapping(target = "balance", source = "balance"),
    })
    List<BusinessWalletDto> businessWalletListToBusinessWalletDtoList(List<BusinessWallet> walletList);
}
