package by.panic.entomus.controller;

import by.panic.entomus.payload.payment.CreatePaymentQrRequest;
import by.panic.entomus.payload.payment.CreatePaymentRequest;
import by.panic.entomus.payload.ExceptionHandler;
import by.panic.entomus.payload.payment.CreatePaymentResponse;
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

    @PostMapping("/payment/qr")
    @Operation(description = "Generate a QR-code for the invoice address")
    @Parameter(in = ParameterIn.HEADER, name = "X-API-KEY",
            description = "We get the 'X-API-KEY' in the response of the /api/v1/merchant",
            required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "We have get QR-code for the invoice address")
    })
    public ResponseEntity<byte[]> createQr(@RequestHeader(name = "X-API-KEY") String apiKey,
                                                  @Validated @RequestBody CreatePaymentQrRequest createPaymentQrRequest) {
        return paymentService.createQr(apiKey, createPaymentQrRequest);
    }

    @Operation(description = "Creating an invoice")
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Incorrect X-API-KEY",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))})
    })
    @PostMapping("/payment")
    public CreatePaymentResponse createInvoice(@RequestHeader(name = "X-API-KEY") String apiKey,
                                               @Validated @RequestBody CreatePaymentRequest createPaymentRequest) {
        return paymentService.create(apiKey, createPaymentRequest);
    }
}
