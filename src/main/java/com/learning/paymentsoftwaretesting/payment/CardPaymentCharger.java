package com.learning.paymentsoftwaretesting.payment;

import java.math.BigDecimal;

public interface CardPaymentCharger {

        CardPaymentCharge chargeCard(
                String cardSource,
                BigDecimal amount,
                Currency ccy,
                String description
        );
}
