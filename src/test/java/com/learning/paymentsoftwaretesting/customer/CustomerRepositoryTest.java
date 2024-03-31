package com.learning.paymentsoftwaretesting.customer;

import com.learning.paymentsoftwaretesting.mapper.CustomerMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    private final CustomerRepository underTest;

    @Autowired
    public CustomerRepositoryTest(CustomerRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    void shouldGetCustomerByPhoneNo() {
        // Given
        Customer customer = CustomerMapper.INSTANCE.mapToCustomer(CustomerRegistrationRequest
                .builder()
                .phoneNo("010384766")
                .name("John Doe")
                .email("jonh@gmail.com")
                .build()
        );

        underTest.save(customer);

        // When
        Optional<Customer> optionalCustomer = underTest.findCustomerByPhoneNo(customer.getPhoneNo());

        // Then
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c).isEqualTo(customer);
                });
    }

    @Test
    void shouldNotGetCustomerByPhoneNoWhenNumberDoesNotExists() {
        // Given
        String phoneNumber = "0000";

        // When
        Optional<Customer> optionalCustomer = underTest.findCustomerByPhoneNo(phoneNumber);

        // Then
        assertThat(optionalCustomer).isNotPresent();
    }

    @Test
    void shouldSaveCustomer() {
        // Given
        Customer customer = CustomerMapper.INSTANCE.mapToCustomer(CustomerRegistrationRequest
                .builder()
                .phoneNo("010384766")
                .name("John Doe")
                .email("jonh@gmail.com")
                .build()
        );

        // When
        underTest.save(customer);

        // Then
        Optional<Customer> optionalCustomer = underTest.findById(customer.getId());
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c).isEqualTo(customer);
                });
    }

    @Test
    void shouldNotSaveCustomerWhenNameIsNull() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, null, "0000", "");

        // When
        // Then
        assertThatThrownBy(() -> underTest.save(customer))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value : com.learning.paymentsoftwaretesting.customer.Customer.name");
    }

    @ParameterizedTest
    @MethodSource("listInvalidCustomer")
    void shouldNotSaveCustomerWhenRequireFieldsNull(Customer customer) {
        // When
        // Then
        assertThatThrownBy(() -> underTest.save(customer))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("not-null property references a null or transient value");
    }

    static Stream<Customer> listInvalidCustomer() {
        return Stream.of(
                new Customer(UUID.randomUUID(), null, "0000", ""),
                new Customer(UUID.randomUUID(), "John Doe", null, "")
        );
    }
}