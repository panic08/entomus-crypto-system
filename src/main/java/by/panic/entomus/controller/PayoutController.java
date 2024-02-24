package by.panic.entomus.controller;

import by.panic.entomus.payload.payout.*;
import by.panic.entomus.payload.ExceptionHandler;
import by.panic.entomus.security.MerchantSecurity;
import by.panic.entomus.service.implement.PayoutServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payout")
@Tag(name = "Payouts", description = "This component describes the Payouts API")
@RequiredArgsConstructor
public class PayoutController {

    private final MerchantSecurity merchantSecurity;
    private final PayoutServiceImpl payoutService;

    @Operation(description = "Creating a payout")
    @Parameter(in = ParameterIn.HEADER, name = "X-API-KEY",
            description = "We got the 'X-API-KEY' in the response of the /api/v1/merchant",
            required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You have successfully created an Payout and received a \"CreatePayoutResponse\"",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CreatePayoutResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Incorrect X-API-KEY",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))})
    })
    @PostMapping
    public CreatePayoutResponse create(@RequestHeader(name = "X-API-KEY") String apiKey,
                                       @Validated @RequestBody CreatePayoutRequest createPayoutRequest) {
        merchantSecurity.checkOnCorrectMerchant(apiKey);
        return payoutService.create(apiKey, createPayoutRequest);
    }

    @Operation(description = "Get payout information")
    @Parameter(in = ParameterIn.HEADER, name = "X-API-KEY",
            description = "We got the 'X-API-KEY' in the response of the /api/v1/merchant",
            required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You have successfully received payout information",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GetPayoutInfoResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Incorrect X-API-KEY || Payout with this order_id already exists || The amount for create payout is too small || Not enough funds || Not enough funds for Subtract = true",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))})
    })
    @GetMapping("/info")
    public GetPayoutInfoResponse getInfo(@RequestHeader(name = "X-API-KEY") String apiKey,
                                         @RequestParam(name = "uuid", required = false) String uuid,
                                         @RequestParam(name = "order_id", required = false) String orderId) {
        merchantSecurity.checkOnCorrectMerchant(apiKey);
        return payoutService.getInfo(apiKey, uuid, orderId);
    }

    @Operation(description = "Get payout history")
    @Parameter(in = ParameterIn.HEADER, name = "X-API-KEY",
            description = "We got the 'X-API-KEY' in the response of the /api/v1/merchant",
            required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You have successfully received payout history",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GetPayoutHistoryResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Incorrect X-API-KEY || Provide uuid or order_id",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))})
    })
    @GetMapping("/list")
    public GetPayoutHistoryResponse getHistory(@RequestHeader(name = "X-API-KEY") String apiKey,
                                               @RequestParam(value = "date_from", required = false) Long dateFrom,
                                               @RequestParam(value = "date_to", required = false) Long dateTo) {
        merchantSecurity.checkOnCorrectMerchant(apiKey);
        return payoutService.getHistory(apiKey, dateFrom, dateTo);
    }

    @Operation(description = "Get list of payout services")
    @Parameter(in = ParameterIn.HEADER, name = "X-API-KEY",
            description = "We got the 'X-API-KEY' in the response of the /api/v1/merchant",
            required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You have successfully get list of payout services",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GetPayoutServiceResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Incorrect X-API-KEY",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))})
    })
    @GetMapping("/services")
    public GetPayoutServiceResponse getServices(@RequestHeader(name = "X-API-KEY") String apiKey) {
        merchantSecurity.checkOnCorrectMerchant(apiKey);
        return payoutService.getServices(apiKey);
    }
}
