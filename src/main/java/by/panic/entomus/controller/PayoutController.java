package by.panic.entomus.controller;

import by.panic.entomus.payload.payout.CreatePayoutResponse;
import by.panic.entomus.payload.payout.CreatePayoutRequest;
import by.panic.entomus.payload.ExceptionHandler;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payout")
@Tag(name = "Payouts", description = "This component describes the Payouts API")
@RequiredArgsConstructor
public class PayoutController {

    private final PayoutServiceImpl payoutService;

    @Operation(description = "Creating a payout")
    @Parameter(in = ParameterIn.HEADER, name = "X-API-KEY",
            description = "We get the 'X-API-KEY' in the response of the /api/v1/merchant",
            required = true,
            content = @Content(schema = @Schema(implementation = String.class)))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "You have successfully created a payout",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CreatePayoutResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Incorrect X-API-KEY",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))})
    })
    @PostMapping
    public CreatePayoutResponse create(@RequestHeader(name = "X-API-KEY") String apiKey,
                                       @RequestBody CreatePayoutRequest createPayoutRequest) {
        return payoutService.create(apiKey, createPayoutRequest);
    }
}
