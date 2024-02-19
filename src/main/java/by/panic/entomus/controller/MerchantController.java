package by.panic.entomus.controller;

import by.panic.entomus.payload.CreateMerchantResponse;
import by.panic.entomus.service.implement.MerchantServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/merchant")
@Tag(name = "Merchant", description = "This component describes the Merchant API")
@RequiredArgsConstructor
public class MerchantController {
    private final MerchantServiceImpl merchantService;

    @PostMapping
    @Operation(description = "Create merchant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "We successfully created merchant and get his \"api_key\"")
    })
    public CreateMerchantResponse create() {
        return merchantService.create();
    }
}
