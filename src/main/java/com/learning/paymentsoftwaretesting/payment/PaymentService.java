package com.learning.paymentsoftwaretesting.payment;

import com.learning.paymentsoftwaretesting.constant.MessageResponseCode;
import com.learning.paymentsoftwaretesting.customer.CustomerRepository;
import com.learning.paymentsoftwaretesting.exception.AppException;
import com.learning.paymentsoftwaretesting.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final CardPaymentCharger cardPaymentCharger;

    private static final List<Currency> ACCEPTED_CURRENCIES = List.of(Currency.USD, Currency.GBP);

    void chargeCard(PaymentRequest paymentRequest) {
        log.info("Charging card for payment request: {}", paymentRequest);

        // ... find customer
        if (customerRepository.findById(paymentRequest.customerId()).isEmpty()) {
            throw new AppException(MessageResponseCode.RESOURCE_NOT_FOUND, "Customer");
        }

        // ... check if support currency
        if (!ACCEPTED_CURRENCIES.contains(paymentRequest.ccy())) {
            throw new AppException(MessageResponseCode.CURRENCY_NOT_SUPPORTED, paymentRequest.ccy().toString());
        }

        // ... charge card
        CardPaymentCharge cardPaymentCharge = cardPaymentCharger.chargeCard(
                paymentRequest.source(),
                paymentRequest.amount(),
                paymentRequest.ccy(),
                paymentRequest.description()
        );

        // ... if not debited throw
        if (!cardPaymentCharge.isCardDebited()) {
            throw new AppException(MessageResponseCode.CARD_NOT_DEBITED, paymentRequest.customerId().toString());
        }

        // ... insert payment
        paymentRepository.save(PaymentMapper.INSTANCE.mapToPayment(paymentRequest));

        // ... send email
    }
}
