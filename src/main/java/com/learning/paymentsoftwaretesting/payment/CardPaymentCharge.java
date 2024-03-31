package com.learning.paymentsoftwaretesting.payment;

import lombok.Builder;

@Builder
public record CardPaymentCharge(boolean isCardDebited) {}
