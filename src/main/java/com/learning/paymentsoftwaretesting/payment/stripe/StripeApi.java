package com.learning.paymentsoftwaretesting.payment.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import com.stripe.param.ChargeCreateParams;
import org.springframework.stereotype.Service;

@Service
public class StripeApi {
    public Charge create(ChargeCreateParams params, RequestOptions requestOptions) throws StripeException {
        return Charge.create(params, requestOptions);
    }
}
