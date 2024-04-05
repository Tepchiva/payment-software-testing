package com.learning.paymentsoftwaretesting.payment.stripe;

import com.learning.paymentsoftwaretesting.constant.MessageResponseCode;
import com.learning.paymentsoftwaretesting.exception.AppException;
import com.learning.paymentsoftwaretesting.payment.CardPaymentCharge;
import com.learning.paymentsoftwaretesting.payment.Currency;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import com.stripe.param.ChargeCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;

import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(
        properties = {
                "stripe.enabled=false"
        }
)
class StripeServiceTest {

    private StripeService underTest;
    @Mock
    private StripeApi stripeApi;

    @Mock
    private StripeException stripeException;

    @Captor
    private ArgumentCaptor<ChargeCreateParams> chargeCreateParamsArgumentCaptor;

    @Captor
    private ArgumentCaptor<RequestOptions> requestOptionsArgumentCaptor;

    @BeforeEach
    void setUp() {
        this.underTest = new StripeService(stripeApi, 50L);
        this.chargeCreateParamsArgumentCaptor = ArgumentCaptor.forClass(ChargeCreateParams.class);
        this.requestOptionsArgumentCaptor = ArgumentCaptor.forClass(RequestOptions.class);
    }

    @Test
    void shouldChargeCardSuccessfully() throws StripeException {
        // Given
        String cardSource = "0x0x0x";
        BigDecimal amount = new BigDecimal("10.00");
        Currency currency = Currency.USD;
        String description = "Unit test";

        // ...Successful charge
        Charge charge = new Charge();
        charge.setPaid(true);
        given(stripeApi.create(any(ChargeCreateParams.class), any())).willReturn(charge);

        // When
        CardPaymentCharge cardPaymentCharge = this.underTest.chargeCard(cardSource, amount, currency, description);

        // Then
        // ... capture the arguments
        then(stripeApi).should().create(chargeCreateParamsArgumentCaptor.capture(), requestOptionsArgumentCaptor.capture());
        ChargeCreateParams chargeCreateParams = chargeCreateParamsArgumentCaptor.getValue();

        assertThat(chargeCreateParams).isNotNull();
        assertThat(cardSource).isEqualTo(chargeCreateParams.getSource());
        assertThat(amount.multiply(BigDecimal.valueOf(100L)).longValue()).isEqualTo(chargeCreateParams.getAmount());
        assertThat(currency.toString()).hasToString(chargeCreateParams.getCurrency());
        assertThat(description).isEqualTo(chargeCreateParams.getDescription());

        RequestOptions requestOptions = requestOptionsArgumentCaptor.getValue();
        assertThat(requestOptions).isNotNull();

        assertThat(cardPaymentCharge).isNotNull();
        assertThat(cardPaymentCharge.isCardDebited()).isTrue();
    }

    @Test
    void shouldThrownAndNotChargeCardWhenLessThanMinAmount() throws StripeException {
        // Given
        String cardSource = "0x0x0x";
        BigDecimal amount = new BigDecimal("0.49");
        Currency currency = Currency.USD;
        String description = "Unit test";

        // When
        // Then
        assertThatThrownBy(() -> this.underTest.chargeCard(cardSource, amount, currency, description))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(MessageResponseCode.AMOUNT_BELOW_MINIMUM.getMessage().formatted("50"));

        then(stripeApi).should(never()).create(any(ChargeCreateParams.class), any());
        then(stripeApi).shouldHaveNoInteractions();
    }

    @Test
    void shouldThrownAndNotChargeCardWhenApiThrowsException() throws StripeException {
        // Given
        String cardSource = "0x0x0x";
        BigDecimal amount = new BigDecimal("10.00");
        Currency currency = Currency.USD;
        String description = "Unit test";

        // ... Throw exception when stripe api is called
        doThrow(stripeException).when(stripeApi).create(any(ChargeCreateParams.class), any());

        // When
        // Then
        assertThatThrownBy(() -> this.underTest.chargeCard(cardSource, amount, currency, description))
                .isInstanceOf(AppException.class)
                .hasRootCause(stripeException)
                .hasMessageContaining(MessageResponseCode.FAILED_PAYMENT.getMessage());
    }
}