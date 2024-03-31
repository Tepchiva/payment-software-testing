package com.learning.paymentsoftwaretesting.payment;

import com.learning.paymentsoftwaretesting.constant.MessageResponseCode;
import com.learning.paymentsoftwaretesting.customer.Customer;
import com.learning.paymentsoftwaretesting.customer.CustomerRepository;
import com.learning.paymentsoftwaretesting.exception.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CardPaymentCharger cardPaymentCharger;

    @Captor
    private ArgumentCaptor<Payment> paymentArgumentCaptor;

    private PaymentService underTest;

    @BeforeEach
    void setUp() {
        this.underTest = new PaymentService(paymentRepository, customerRepository, cardPaymentCharger);
        this.paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
    }

    @Test
    void shouldChargeCardSuccessfully() {
        // Given
        PaymentRequest paymentRequest = new PaymentRequest(
                UUID.randomUUID(),
                new BigDecimal("10.00"),
                Currency.USD,
                "234xxxxx",
                "Unit test"
        );

        // ... Exist customer by id
        given(customerRepository.findById(paymentRequest.customerId())).willReturn(Optional.of(mock(Customer.class)));

        // ... Card is charged successfully
        given(cardPaymentCharger.chargeCard(
                paymentRequest.source(),
                paymentRequest.amount(),
                paymentRequest.ccy(),
                paymentRequest.description()
        )).willReturn(new CardPaymentCharge(true));

        // When
        underTest.chargeCard(paymentRequest);

        // Then
        then(paymentRepository).should().save(paymentArgumentCaptor.capture());
        Payment paymentSaved = paymentArgumentCaptor.getValue();

        assertThat(paymentRequest).satisfies(request -> {
            assertThat(request.amount()).isEqualTo(paymentSaved.getAmount());
            assertThat(request.ccy()).isEqualTo(paymentSaved.getCcy());
            assertThat(request.customerId()).isEqualTo(paymentSaved.getCustomerId());
            assertThat(request.source()).isEqualTo(paymentSaved.getSource());
            assertThat(request.description()).isEqualTo(paymentSaved.getDescription());
        });
    }

    @Test
    void shouldNotChargeAndThrownWhenCustomerNotFound() {
        // Given
        PaymentRequest paymentRequest = new PaymentRequest(UUID.randomUUID(), BigDecimal.valueOf(1.00), Currency.USD, "23xxx", "unit test");
        // ... can use given()
        when(customerRepository.findById(paymentRequest.customerId())).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargeCard(paymentRequest))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(MessageResponseCode.RESOURCE_NOT_FOUND.getMessage().formatted("Customer"));

        then(customerRepository).shouldHaveNoMoreInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
        then(cardPaymentCharger).shouldHaveNoInteractions();
    }

    @Test
    void shouldNotChargeAndThrownWhenCurrencyNotSupported() {
        // Given
        PaymentRequest paymentRequest = new PaymentRequest(
                UUID.randomUUID(),
                new BigDecimal("10.00"),
                Currency.KHR,
                "234xxxxx",
                "Unit test"
        );

        // ... Exist customer by id
        given(customerRepository.findById(paymentRequest.customerId())).willReturn(Optional.of(mock(Customer.class)));

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargeCard(paymentRequest))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(MessageResponseCode.CURRENCY_NOT_SUPPORTED.getMessage().formatted(paymentRequest.ccy().toString()));

        then(customerRepository).shouldHaveNoMoreInteractions();
        then(paymentRepository).shouldHaveNoInteractions();
        then(cardPaymentCharger).shouldHaveNoInteractions();
        then(paymentRepository).should(never()).save(any(Payment.class));
    }

    @Test
    void shouldThrownWhenCardIdNotCharged() {
        // Given
        PaymentRequest paymentRequest = new PaymentRequest(
                UUID.randomUUID(),
                new BigDecimal("10.00"),
                Currency.USD,
                "234xxxxx",
                "Unit test"
        );

        // ... Exist customer by id
        given(customerRepository.findById(paymentRequest.customerId())).willReturn(Optional.of(mock(Customer.class)));

        // ... Card is charged successfully
        given(cardPaymentCharger.chargeCard(
                paymentRequest.source(),
                paymentRequest.amount(),
                paymentRequest.ccy(),
                paymentRequest.description()
        )).willReturn(new CardPaymentCharge(false));

        // When
        // Then
        assertThatThrownBy(() -> underTest.chargeCard(paymentRequest))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(MessageResponseCode.CARD_NOT_DEBITED.getMessage().formatted(paymentRequest.customerId()));

        then(paymentRepository).shouldHaveNoInteractions();
    }
}
