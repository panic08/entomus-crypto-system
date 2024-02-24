package by.panic.entomus.mapper;

import by.panic.entomus.dto.PayoutDto;
import by.panic.entomus.entity.Payout;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PayoutToPayoutDtoMapper {
    @Mappings({
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "uuid", source = "uuid"),
            @Mapping(target = "orderId", source = "orderId"),
            @Mapping(target = "amount", source = "amount"),
            @Mapping(target = "network", source = "network"),
            @Mapping(target = "token", source = "token"),
            @Mapping(target = "address", source = "address"),
            @Mapping(target = "txId", source = "txId"),
            @Mapping(target = "balance", source = "balance"),
            @Mapping(target = "final", source = "isFinal"),
            @Mapping(target = "urlCallback", source = "urlCallback"),
            @Mapping(target = "createdAt", source = "createdAt"),
    })
    PayoutDto payoutToPayoutDto(Payout payout);

    @Mappings({
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "uuid", source = "uuid"),
            @Mapping(target = "orderId", source = "orderId"),
            @Mapping(target = "amount", source = "amount"),
            @Mapping(target = "network", source = "network"),
            @Mapping(target = "token", source = "token"),
            @Mapping(target = "address", source = "address"),
            @Mapping(target = "txId", source = "txId"),
            @Mapping(target = "balance", source = "balance"),
            @Mapping(target = "final", source = "isFinal"),
            @Mapping(target = "urlCallback", source = "urlCallback"),
            @Mapping(target = "createdAt", source = "createdAt"),
    })
    List<PayoutDto> payoutListToPayoutDtoList(List<Payout> payoutList);
}
