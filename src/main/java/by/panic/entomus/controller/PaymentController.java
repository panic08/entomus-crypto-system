package by.panic.entomus.controller;

import by.panic.entomus.payload.CreatePaymentQrRequest;
import by.panic.entomus.service.implement.PaymentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Payments", description = "This component describes the Payments API")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentServiceImpl paymentService;

    @PostMapping("/payment/qr")
    @Operation(description = "Generate a QR-code for the invoice address")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "We have get QR-code for the invoice address")
    })
    public ResponseEntity<byte[]> createPaymentQr(@RequestHeader("X-API-KEY") String apiKey,
                                                  @RequestBody CreatePaymentQrRequest createPaymentQrRequest) {
        return paymentService.createPaymentQr(apiKey, createPaymentQrRequest);
    }
}
