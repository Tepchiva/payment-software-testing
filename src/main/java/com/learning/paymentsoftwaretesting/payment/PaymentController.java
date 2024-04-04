package com.learning.paymentsoftwaretesting.payment;

import com.learning.paymentsoftwaretesting.exception.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<SuccessResponse<PaymentResponse>> makePayment(@RequestBody @Validated PaymentRequest paymentRequest) {
        return ResponseEntity.ok(new SuccessResponse<>(paymentService.chargeCard(paymentRequest)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<PaymentResponse>> getPayment(@PathVariable Long id) {
        PaymentResponse paymentResponse = paymentService.getPayment(id);
        return ResponseEntity.ok(new SuccessResponse<>(paymentResponse));
    }
}
