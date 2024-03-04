package by.panic.entomus.controller.payment;

import by.panic.entomus.payload.payment.staticWallet.StaticWalletTransactionWebHookRequest;
import by.panic.entomus.service.implement.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/callback/static-wallet")
@RequiredArgsConstructor
public class PaymentCallbackController {
    private final PaymentServiceImpl paymentService;

    @PostMapping
    public void handleStaticWalletTransaction(@RequestBody StaticWalletTransactionWebHookRequest staticWalletTransactionWebHookRequest) {
        paymentService.handleStaticWalletWebHook(staticWalletTransactionWebHookRequest);
    }
}
