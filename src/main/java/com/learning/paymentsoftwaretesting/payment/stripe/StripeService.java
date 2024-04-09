package com.learning.paymentsoftwaretesting.payment.stripe;

import com.learning.paymentsoftwaretesting.constant.MessageResponseCode;
import com.learning.paymentsoftwaretesting.exception.AppException;
import com.learning.paymentsoftwaretesting.payment.CardPaymentCharge;
import com.learning.paymentsoftwaretesting.payment.CardPaymentCharger;
import com.learning.paymentsoftwaretesting.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import com.stripe.param.ChargeCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@Slf4j
@ConditionalOnProperty(
        value = "stripe.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class StripeService implements CardPaymentCharger {

    private final StripeApi stripeApi;

    @Value("${stripe.api-secret-key}")
    private String apiKey;

    private final Long minAmount;

    @Autowired
    public StripeService(StripeApi stripeApi, @Value("${stripe.min-amount}") Long minAmount) {
        this.stripeApi = stripeApi;
        this.minAmount = minAmount;
    }


    @Override
    public CardPaymentCharge chargeCard(String cardSource, BigDecimal amount, Currency ccy, String description) {

        long amountInCent = amount.multiply(BigDecimal.valueOf(100L)).longValue();

        // ... should throw if amount in cent lower than 50c
        if (amountInCent < minAmount) {
            throw new AppException(MessageResponseCode.AMOUNT_BELOW_MINIMUM, minAmount.toString());
        }

        final RequestOptions requestOption = RequestOptions
                .builder()
                .setApiKey(apiKey)
                .build();

        ChargeCreateParams params = ChargeCreateParams
                .builder()
                .setAmount(amountInCent)
                .setCurrency(ccy.toString())
                .setSource(cardSource)
                .setDescription(description)
                .build();

        try {
            // ... create charge with stripe api
            Charge charge = stripeApi.create(params, requestOption);
            return new CardPaymentCharge(charge.getPaid());
        }
        catch (StripeException ex) {
            log.error("Error occurred while charging card", ex);
            throw new AppException(MessageResponseCode.FAILED_PAYMENT, ex);
        }
    }
}
