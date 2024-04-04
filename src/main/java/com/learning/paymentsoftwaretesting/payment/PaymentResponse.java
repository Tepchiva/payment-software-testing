package com.learning.paymentsoftwaretesting.payment;

import java.util.UUID;

public record PaymentResponse(
        Long id,
        UUID customerId,
        String description,
        String ccy,
        String amount
) {
}
