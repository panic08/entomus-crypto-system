package by.panic.entomus.controller;

import by.panic.entomus.payload.CreateMerchantResponse;
import by.panic.entomus.payload.ExceptionHandler;
import by.panic.entomus.payload.merchant.GetMerchantBalanceResponse;
import by.panic.entomus.security.MerchantSecurity;
import by.panic.entomus.service.implement.MerchantServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/merchant")
@Tag(name = "Merchant", description = "This component describes the Merchant API")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantSecurity merchantSecurity;
    private final MerchantServiceImpl merchantService;

    @PostMapping
    @Operation(description = "Create merchant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "We successfully created merchant and got his \"api_key\"")
    })
    public CreateMerchantResponse create() {
        return merchantService.create();
    }

    @GetMapping("/balance")
    @Operation(description = "Get merchant balance")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "We successfully got merchant balance",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GetMerchantBalanceResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Incorrect X-API-KEY",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))})
    })
    public GetMerchantBalanceResponse getBalance(@RequestHeader("X-API-KEY") String apiKey) {
        merchantSecurity.checkOnCorrectMerchant(apiKey);
        return merchantService.getBalance(apiKey);
    }
}
