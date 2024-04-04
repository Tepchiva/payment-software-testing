package com.learning.paymentsoftwaretesting.payment.stripe;

import com.learning.paymentsoftwaretesting.payment.CardPaymentCharge;
import com.learning.paymentsoftwaretesting.payment.CardPaymentCharger;
import com.learning.paymentsoftwaretesting.payment.Currency;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@ConditionalOnProperty(
        value = "stripe.enabled",
        havingValue = "false",
        matchIfMissing = true
)
public class MockStripeService implements CardPaymentCharger {

    @Override
    public CardPaymentCharge chargeCard(String cardSource, BigDecimal amount, Currency ccy, String description) {
        return new CardPaymentCharge(true);
    }
}
