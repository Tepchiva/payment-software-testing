package com.learning.paymentsoftwaretesting.customer;

import com.learning.paymentsoftwaretesting.constant.MessageResponseCode;
import com.learning.paymentsoftwaretesting.exception.AppException;
import com.learning.paymentsoftwaretesting.mapper.CustomerMapper;
import com.learning.paymentsoftwaretesting.utils.PhoneNumberValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PhoneNumberValidator phoneNumberValidator;

    private CustomerService underTest;
    private ArgumentCaptor<Customer> customerArgumentCaptor;
    // private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        // replace with @ExtendWith(MockitoExtension.class)
        // this.autoCloseable = MockitoAnnotations.openMocks(this);

        this.underTest = new CustomerService(customerRepository, phoneNumberValidator, null);
        this.customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
    }

    @AfterEach
    void tearDown() {
        /*
        try {
            this.autoCloseable.close();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred while closing AutoCloseable", e);
        }
        */
    }

    @Test
    void shouldRegisterNewCustomer() {
        // Given
        CustomerRegistrationRequest registrationRequest = CustomerRegistrationRequest
                .builder()
                .name("John Doe")
                .phoneNo("010384766")
                .email("jonh@gmail.com")
                .build();

        // ... No customer with phone number passed
        given(customerRepository.findCustomerByPhoneNo(registrationRequest.phoneNo())).willReturn(Optional.empty());
        given(phoneNumberValidator.test(registrationRequest.phoneNo())).willReturn(true);

        // When
        underTest.registerNewCustomer(registrationRequest);

        // Then
        // ... Capture the customer object passed to the save method
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();

        // ... Assert the customer object passed to the save method has the same values fields as the registration request
        assertThat(customerArgumentCaptorValue).satisfies(customer -> {
            assertThat(customer.getName()).isEqualTo(registrationRequest.name());
            assertThat(customer.getPhoneNo()).isEqualTo(registrationRequest.phoneNo());
            assertThat(customer.getEmail()).isEqualTo(registrationRequest.email());
        });

        // ... Assert id field of customer object passed to the save method is not null
        assertThat(customerArgumentCaptorValue.getId()).isNotNull();
    }

    @Test
    void shouldNotRegisterNewCustomerWhenCustomerExist() {
        // Given
        CustomerRegistrationRequest registrationRequest = CustomerRegistrationRequest
                .builder()
                .name("John Doe")
                .phoneNo("010384766")
                .build();

        // ... Exist customer with phone number passed
        given(customerRepository.findCustomerByPhoneNo(registrationRequest.phoneNo())).willReturn(Optional.of(CustomerMapper.INSTANCE.mapToCustomer(registrationRequest)));
        given(phoneNumberValidator.test(registrationRequest.phoneNo())).willReturn(true);

        // When
        // Then
        assertThatThrownBy(() -> underTest.registerNewCustomer(registrationRequest))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(MessageResponseCode.CUSTOMER_ALREADY_REGISTERED.getMessage(), registrationRequest.name());

        then(customerRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void shouldThrownExceptionWhenPhoneNoIsTaken() {
        // Given
        CustomerRegistrationRequest registrationRequest = CustomerRegistrationRequest
                .builder()
                .name("John Doe")
                .phoneNo("010384766")
                .build();

        Customer existCustomer = new Customer(UUID.randomUUID(), "Jack", "010384766", "jack@gmail.com");

        // ... Exist customer with phone number passed
        given(customerRepository.findCustomerByPhoneNo(registrationRequest.phoneNo())).willReturn(Optional.of(existCustomer));
        given(phoneNumberValidator.test(registrationRequest.phoneNo())).willReturn(true);

        // When
        // Then
        assertThatThrownBy(() -> underTest.registerNewCustomer(registrationRequest))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Phone number [%s] already registered".formatted(registrationRequest.phoneNo()));

        then(customerRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void shouldGetCustomerByPhoneNo() {
        // Given
        Customer existCustomer = new Customer(UUID.randomUUID(), "John Doe", "010384766", "john@gmail.com");

        // ... Exist customer with phone number passed
        given(customerRepository.findCustomerByPhoneNo(existCustomer.getPhoneNo())).willReturn(Optional.of(existCustomer));
        given(phoneNumberValidator.test(existCustomer.getPhoneNo())).willReturn(true);

        // When
        CustomerResponse customerResponse = underTest.getCustomerByPhoneNo(existCustomer.getPhoneNo());

        // Then
        assertThat(customerResponse).satisfies(response -> {
            assertThat(existCustomer.getId()).isEqualTo(response.id());
            assertThat(existCustomer.getName()).isEqualTo(response.name());
            assertThat(existCustomer.getPhoneNo()).isEqualTo(response.phoneNo());
            assertThat(existCustomer.getEmail()).isEqualTo(response.email());
        });
    }

    @Test
    void shouldThrownExceptionWhenCustomerNotFound() {
        // Given
        String phoneNo = "010384766";

        // ... No customer with phone number passed
        given(customerRepository.findCustomerByPhoneNo(phoneNo)).willReturn(Optional.empty());
        given(phoneNumberValidator.test(phoneNo)).willReturn(true);
        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomerByPhoneNo(phoneNo))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(MessageResponseCode.RESOURCE_NOT_FOUND.getMessage().formatted("Customer"));

        then(customerRepository).should().findCustomerByPhoneNo(phoneNo);
    }

    @Test
    void shouldThrownExceptionWhenPhoneNoIsNotValid() {
        // Given
        String phoneNo = "010384766";

        // ... No customer with phone number passed
        given(phoneNumberValidator.test(phoneNo)).willReturn(false);
        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomerByPhoneNo(phoneNo))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(MessageResponseCode.PHONE_NUMBER_NOT_VALID.getMessage().formatted(phoneNo));

        then(customerRepository).shouldHaveNoInteractions();
    }
}