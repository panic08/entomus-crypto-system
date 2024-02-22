package by.panic.entomus.controller;

import by.panic.entomus.payload.payment.CreatePaymentRequest;
import by.panic.entomus.payload.ExceptionHandler;
import by.panic.entomus.payload.payment.CreatePaymentResponse;
import by.panic.entomus.payload.payment.GetPaymentInfoResponse;
import by.panic.entomus.service.implement.PaymentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Payments", description = "This component describes the Payments API")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentServiceImpl paymentService;

    @Operation(description = "Generate a QR-code for the invoice address")
    @Parameter(in = ParameterIn.HEADER, name = "X-API-KEY",
            description = "We get the 'X-API-KEY' in the response of the /api/v1/merchant",
            required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "We have get QR-code for the invoice address"),
            @ApiResponse(responseCode = "400", description = "Incorrect X-API-KEY",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))}),
            @ApiResponse(responseCode = "404", description = "Invoice not found")
    })
    @GetMapping("/payment/qr")
    public ResponseEntity<byte[]> createQr(@RequestHeader(name = "X-API-KEY") String apiKey,
                                           @RequestParam("invoice_uuid") String invoiceUuid) {
        return paymentService.createQr(apiKey, invoiceUuid);
    }

    @Operation(description = "Creating an invoice")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You have successfully created an Invoice and received a \"CreatePaymentResponse\"",
                    content = {@Content(schema = @Schema(implementation = CreatePaymentResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Incorrect X-API-KEY || Amount not may be null || Currency not may be null " +
                    "|| Network not may be null || Token not may be null || Order_id not may be null " +
                    "|| Order_id must be a string consisting of alphabetic characters, numbers, underscores, and dashes " +
                    "|| Order_id must contain from 1 to 128 characters\" || Url_return must contain from 6 to 255 characters " +
                    "|| Url_success must contain from 6 to 255 characters || Url_callback must contain from 6 to 255 characters " +
                    "|| Lifetime not may be null || Lifetime can contain a number between 300 and 10800 " +
                    "|| Discount_percent can contain a number between -99 and 100 || Additional_data must contain from 0 to 255 characters",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))})
    })
    @Parameter(in = ParameterIn.HEADER, name = "X-API-KEY",
            description = "We get the 'X-API-KEY' in the response of the /api/v1/merchant",
            required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/payment")
    public CreatePaymentResponse createInvoice(@RequestHeader(name = "X-API-KEY") String apiKey,
                                               @Validated @RequestBody CreatePaymentRequest createPaymentRequest) {
        return paymentService.create(apiKey, createPaymentRequest);
    }

    @Operation(description = "Get payment information")
    @Parameter(in = ParameterIn.HEADER, name = "X-API-KEY",
            description = "We get the 'X-API-KEY' in the response of the /api/v1/merchant",
            required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You have successfully received payment information",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GetPaymentInfoResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Provide uuid or orderId",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))})
    })
    @GetMapping("/payment/info")
    public GetPaymentInfoResponse getInvoiceInfo(@RequestHeader("X-API-KEY") String apiKey,
                                                 @RequestParam(value = "uuid", required = false) String uuid,
                                                 @RequestParam(value = "order_id", required = false) String orderId) {
        return paymentService.getInfo(apiKey, uuid, orderId);
    }
}
