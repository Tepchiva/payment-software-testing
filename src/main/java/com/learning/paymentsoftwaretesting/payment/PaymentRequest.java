package com.learning.paymentsoftwaretesting.payment;

import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRequest(
        @NotNull UUID customerId,
        @NotNull BigDecimal amount,
        @NotNull Currency ccy,
        @NotNull String source,
        String description
) {}
