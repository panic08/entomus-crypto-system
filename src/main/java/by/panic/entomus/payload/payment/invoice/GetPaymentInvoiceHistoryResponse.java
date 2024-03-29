package by.panic.entomus.payload.payment.invoice;

import by.panic.entomus.dto.InvoiceDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class GetPaymentInvoiceHistoryResponse {
    private int state;
    private Result result;

    @Getter
    @Setter
    @Builder
    @Schema(name = "GetPaymentHistoryResponseResult")
    public static class Result {
        private List<InvoiceDto> items;
    }
}
