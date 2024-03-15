package by.panic.entomus.controller.payment;

import by.panic.entomus.payload.ExceptionHandler;
import by.panic.entomus.payload.payment.invoice.*;
import by.panic.entomus.payload.payment.staticWallet.BlockStaticWalletRequest;
import by.panic.entomus.payload.payment.staticWallet.BlockStaticWalletResponse;
import by.panic.entomus.payload.payment.staticWallet.CreateStaticWalletRequest;
import by.panic.entomus.payload.payment.staticWallet.CreateStaticWalletResponse;
import by.panic.entomus.security.MerchantSecurity;
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
@RequestMapping("/api/v1/payment")
@Tag(name = "Payments", description = "This component describes the Payments API")
@RequiredArgsConstructor
public class PaymentController {

    private final MerchantSecurity merchantSecurity;
    private final PaymentServiceImpl paymentService;

    @Operation(description = "Generate a QR-code for the payment invoice address")
    @Parameter(in = ParameterIn.HEADER, name = "X-API-KEY",
            description = "We got the 'X-API-KEY' in the response of the /api/v1/merchant",
            required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "We have got QR-code for the payment invoice address"),
            @ApiResponse(responseCode = "400", description = "Incorrect X-API-KEY",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))}),
            @ApiResponse(responseCode = "404", description = "Invoice not found")
    })
    @GetMapping("/invoice/qr")
    public ResponseEntity<byte[]> createInvoiceQr(@RequestHeader(name = "X-API-KEY") String apiKey,
                                                  @RequestParam("uuid") String uuid) {
        merchantSecurity.checkOnCorrectMerchant(apiKey);
        return paymentService.createInvoiceQr(apiKey, uuid);
    }

    @Operation(description = "Creating an payment invoice")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You have successfully created an payment invoice and received a \"CreatePaymentInvoiceResponse\"",
                    content = {@Content(schema = @Schema(implementation = CreatePaymentInvoiceResponse.class))}),
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
            description = "We got the 'X-API-KEY' in the response of the /api/v1/merchant",
            required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @PostMapping("/invoice")
    public CreatePaymentInvoiceResponse createInvoice(@RequestHeader(name = "X-API-KEY") String apiKey,
                                                      @Validated @RequestBody CreatePaymentInvoiceRequest createPaymentInvoiceRequest) {
        merchantSecurity.checkOnCorrectMerchant(apiKey);
        return paymentService.createInvoice(apiKey, createPaymentInvoiceRequest);
    }

    @Operation(description = "Get payment invoice information")
    @Parameter(in = ParameterIn.HEADER, name = "X-API-KEY",
            description = "We got the 'X-API-KEY' in the response of the /api/v1/merchant",
            required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You have successfully received payment invoice information",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GetPaymentInvoiceInfoResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Incorrect X-API-KEY || You do not have a invoice with that uuid or order_id",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))})
    })
    @GetMapping("/invoice/info")
    public GetPaymentInvoiceInfoResponse getInvoiceInfo(@RequestHeader("X-API-KEY") String apiKey,
                                                        @RequestParam(value = "uuid", required = false) String uuid,
                                                        @RequestParam(value = "order_id", required = false) String orderId) {
        merchantSecurity.checkOnCorrectMerchant(apiKey);

        return paymentService.getInvoiceInfo(apiKey, uuid, orderId);
    }

    @Operation(description = "Get payment invoice history")
    @Parameter(in = ParameterIn.HEADER, name = "X-API-KEY",
            description = "We got the 'X-API-KEY' in the response of the /api/v1/merchant",
            required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You have successfully received payment invoice history",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GetPaymentInvoiceHistoryResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Incorrect X-API-KEY",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))})
    })
    @GetMapping("/invoice/list")
    public GetPaymentInvoiceHistoryResponse getInvoiceHistory(@RequestHeader("X-API-KEY") String apiKey,
                                                              @RequestParam(value = "date_from", required = false) Long dateFrom,
                                                              @RequestParam(value = "date_to", required = false) Long dateTo) {
        merchantSecurity.checkOnCorrectMerchant(apiKey);

        return paymentService.getInvoiceHistory(apiKey, dateFrom, dateTo);
    }

    @Operation(description = "Resend payment invoice webhook")
    @Parameter(in = ParameterIn.HEADER, name = "X-API-KEY",
            description = "We got the 'X-API-KEY' in the response of the /api/v1/merchant",
            required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You have successfully resend payment invoice webhook",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResendPaymentInvoiceWebHookResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Incorrect X-API-KEY || You do not have a invoice with that uuid or order_id || Invoice has not yet received \"SUCCESS\" status || Invoice does not specify url_callback",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))})
    })
    @PostMapping("/invoice/resend-webhook")
    public ResendPaymentInvoiceWebHookResponse resendWebHook(@RequestHeader("X-API-KEY") String apiKey,
                                                             @RequestBody ResendPaymentInvoiceWebHookRequest resendPaymentInvoiceWebhookRequest) {
        merchantSecurity.checkOnCorrectMerchant(apiKey);
        return paymentService.resendInvoiceWebHook(apiKey, resendPaymentInvoiceWebhookRequest);
    }

    @Operation(description = "Testing payment invoice webhook")
    @PostMapping("/invoice/test-webhook")
    @Parameter(in = ParameterIn.HEADER, name = "X-API-KEY",
            description = "We got the 'X-API-KEY' in the response of the /api/v1/merchant",
            required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You have successfully test payment invoice webhook",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TestPaymentInvoiceWebHookResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Incorrect X-API-KEY || Status not may be null || Order_id not may be null || Order_id must be a string consisting of alphabetic characters, numbers, underscores, and dashes || Order_id must contain from 1 to 128 characters || Merchant_amount not may be null || Network not may be null || Token not may be null || Url_callback must contain from 6 to 255 characters",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))})
    })
    public TestPaymentInvoiceWebHookResponse testInvoiceWebHook(@RequestHeader("X-API-KEY") String apiKey,
                                                                @Validated @RequestBody TestPaymentInvoiceWebHookRequest testPaymentInvoiceWebHookRequest) {
        merchantSecurity.checkOnCorrectMerchant(apiKey);
        return paymentService.testInvoiceWebHook(apiKey, testPaymentInvoiceWebHookRequest);
    }

    @Operation(description = "Get list of payment invoice services")
    @Parameter(in = ParameterIn.HEADER, name = "X-API-KEY",
            description = "We got the 'X-API-KEY' in the response of the /api/v1/merchant",
            required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You have successfully got list of payment invoice services",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GetPaymentInvoiceServiceResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Incorrect X-API-KEY",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))})
    })
    @GetMapping("/invoice/services")
    public GetPaymentInvoiceServiceResponse getInvoiceServices(@RequestHeader("X-API-KEY") String apiKey) {
        merchantSecurity.checkOnCorrectMerchant(apiKey);
        return paymentService.getInvoiceServices(apiKey);
    }

    @Operation(description = "Creating a payment static wallet")
    @Parameter(in = ParameterIn.HEADER, name = "X-API-KEY",
            description = "We got the 'X-API-KEY' in the response of the /api/v1/merchant",
            required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You have successfully created an payment static wallet and received a \"CreateStaticWalletResponse\"",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CreateStaticWalletResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Incorrect X-API-KEY || Network not may be null || Token not may be null || Order_id not may be null || Order_id must be a string consisting of alphabetic characters, numbers, underscores, and dashes " +
                    "|| Order_id must contain from 1 to 128 characters || Url_callback must contain from 6 to 255 characters " +
                    "|| Payment with this order_id already exists || You have entered an invalid Network-Token pair",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))})
    })
    @PostMapping("/static-wallet")
    public CreateStaticWalletResponse createStaticWallet(@RequestHeader("X-API-KEY") String apiKey,
                                                         @Validated @RequestBody CreateStaticWalletRequest createStaticWalletRequest) {
        merchantSecurity.checkOnCorrectMerchant(apiKey);

        return paymentService.createStaticWallet(apiKey, createStaticWalletRequest);
    }

    @Operation(description = "Block payment static wallet")
    @Parameter(in = ParameterIn.HEADER, name = "X-API-KEY",
            description = "We got the 'X-API-KEY' in the response of the /api/v1/merchant",
            required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You have successfully block payment static wallet",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CreateStaticWalletResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Incorrect X-API-KEY || You do not have a static wallet with that uuid or order_id",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))})
    })
    @PostMapping("/static-wallet/block")
    public BlockStaticWalletResponse blockStaticWallet(@RequestHeader("X-API-KEY") String apiKey,
                                                       @RequestBody BlockStaticWalletRequest blockStaticWalletRequest) {
        merchantSecurity.checkOnCorrectMerchant(apiKey);

        return paymentService.blockStaticWallet(apiKey, blockStaticWalletRequest);
    }

    @Operation(description = "Generate a QR-code for the payment static wallet address")
    @Parameter(in = ParameterIn.HEADER, name = "X-API-KEY",
            description = "We got the 'X-API-KEY' in the response of the /api/v1/merchant",
            required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "We have got QR-code for the payment static wallet address"),
            @ApiResponse(responseCode = "400", description = "Incorrect X-API-KEY",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))}),
            @ApiResponse(responseCode = "404", description = "Static wallet not found")
    })
    @GetMapping("/static-wallet/qr")
    public ResponseEntity<byte[]> createStaticWalletQr(@RequestHeader("X-API-KEY") String apiKey,
                                                       @RequestParam("uuid") String uuid) {
        merchantSecurity.checkOnCorrectMerchant(apiKey);

        return paymentService.createStaticWalletQr(apiKey, uuid);
    }
}
