package com.apiator.shop.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/{id}")
    public String createPaymentRequest(@PathVariable(name = "id") long itemId){
        return paymentService.createPaymentRequest(itemId);
    }

    @PostMapping("/{id}")
    public void purchaseItem(@AuthenticationPrincipal Jwt currentUserJwt, @PathVariable(name = "id") long paymentId){
        paymentService.purchaseItem(currentUserJwt, paymentId);
    }
}
