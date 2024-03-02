package by.panic.entomus.controller;

import by.panic.entomus.entity.enums.CryptoToken;
import by.panic.entomus.payload.ExceptionHandler;
import by.panic.entomus.payload.exchangeRate.GetExchangeRateListResponse;
import by.panic.entomus.service.implement.ExchangeRateServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exchange-rate")
@Tag(name = "Exchange rates", description = "This component describes the Exchange rates API")
@RequiredArgsConstructor
public class ExchangeController {
    private final ExchangeRateServiceImpl exchangeRateService;

    @Operation(description = "Get exchange-rate list")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "We successfully got exchange-rate list",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = GetExchangeRateListResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Incorrect X-API-KEY",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionHandler.class))})
    })
    @GetMapping("/{token}/list")
    public GetExchangeRateListResponse getList(@RequestHeader("X-API-KEY") String apiKey,
                                               @PathVariable("token") CryptoToken from) {
        return exchangeRateService.getList(apiKey, from);
    }
}
