package by.panic.entomus.mapper;

import by.panic.entomus.dto.InvoiceDto;
import by.panic.entomus.entity.Invoice;
import by.panic.entomus.enums.CryptoNetwork;
import by.panic.entomus.enums.CryptoToken;
import by.panic.entomus.enums.InvoiceStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface InvoiceToInvoiceDtoMapper {
    @Mappings({
            @Mapping(target = "status", source = "status"),
            @Mapping(target = "uuid", source = "uuid"),
            @Mapping(target = "orderId", source = "orderId"),
            @Mapping(target = "amount", source = "amount"),
            @Mapping(target = "discount", source = "discount"),
            @Mapping(target = "discountPercent", source = "discountPercent"),
            @Mapping(target = "currency", source = "currency"),
            @Mapping(target = "merchantAmount", source = "merchantAmount"),
            @Mapping(target = "network", source = "network"),
            @Mapping(target = "token", source = "token"),
            @Mapping(target = "address", source = "address"),
            @Mapping(target = "txId", source = "txId"),
            @Mapping(target = "additionalData", source = "additionalData"),
            @Mapping(target = "urlReturn", source = "urlReturn"),
            @Mapping(target = "urlSuccess", source = "urlSuccess"),
            @Mapping(target = "final", source = "isFinal"),
            @Mapping(target = "expiredAt", source = "expiredAt"),
            @Mapping(target = "updatedAt", source = "updatedAt"),

            @Mapping(target = "createdAt", source = "createdAt"),
    })
    InvoiceDto invoiceToInvoiceDto(Invoice invoice);
}


