package by.panic.entomus.mapper;

import by.panic.entomus.dto.StaticWalletDto;
import by.panic.entomus.entity.StaticWallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface StaticWalletToStaticWalletDtoMapper {
    @Mappings({
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "uuid", source = "uuid"),
            @Mapping(target = "orderId", source = "orderId"),
            @Mapping(target = "address", source = "address"),
            @Mapping(target = "network", source = "network"),
            @Mapping(target = "token", source = "token"),
            @Mapping(target = "urlCallback", source = "urlCallback"),
            @Mapping(target = "createdAt", source = "createdAt"),
    })
    StaticWalletDto staticWalletToStaticWalletDto(StaticWallet staticWallet);
}
